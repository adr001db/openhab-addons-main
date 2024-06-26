package org.openhab.binding.onecta.internal.api;

import static org.openhab.binding.onecta.internal.OnectaBridgeConstants.CHANNEL_LOGRAWDATA;
import static org.openhab.binding.onecta.internal.OnectaBridgeConstants.CHANNEL_STUBDATAFILE;
import static org.openhab.binding.onecta.internal.api.OnectaProperties.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.onecta.internal.OnectaConfiguration;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandOnOf;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandTrueFalse;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;
import org.openhab.binding.onecta.internal.api.dto.units.Units;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationException;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationForbiddenException;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OnectaConnectionClient {

    static private final Logger logger = LoggerFactory.getLogger(OnectaSignInClient.class);
    public static final String HTTPHEADER_X_API_KEY = "x-api-key";
    public static final String HTTPHEADER_BEARER = "Bearer %s";
    public static final String USER_AGENT_VALUE = "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0";
    public static final String HTTPHEADER_X_API_KEY_VALUE = "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC";
    static private JsonArray rawData = new JsonArray();
    static private Units onectaData = new Units();

    public static Units getUnits() {
        return onectaData;
    }

    private static OnectaSignInClient onectaSignInClient;

    public static void SetConnectionClient(HttpClientFactory httpClientFactory) {
        // httpClient = httpClientFactory.getCommonHttpClient();
        onectaSignInClient = new OnectaSignInClient();
    }

    public static void startConnecton(String userId, String password, String refreshToken)
            throws DaikinCommunicationException {
        onectaSignInClient.signIn(userId, password, refreshToken);
    }

    public static Boolean isOnline() {
        return onectaSignInClient.isOnline();
    }

    private static Response doBearerRequestGet(Boolean refreshed) throws DaikinCommunicationException {
        Response response = null;
        logger.debug(String.format("doBearerRequestGet : refreshed %s", refreshed.toString()));
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            response = OnectaConfiguration.getHttpClient().newRequest(OnectaProperties.getBaseUrl(""))
                    .method(HttpMethod.GET)
                    .header(HttpHeader.AUTHORIZATION, String.format(HTTPHEADER_BEARER, onectaSignInClient.getToken()))
                    .header(HttpHeader.USER_AGENT, USER_AGENT_VALUE)
                    .header(HTTPHEADER_X_API_KEY, HTTPHEADER_X_API_KEY_VALUE).send();

            if (response.getStatus() == HttpStatus.UNAUTHORIZED_401 && !refreshed) {
                onectaSignInClient.fetchAccessToken();
                response = doBearerRequestGet(true);
            }

        } catch (Exception e) {
            if (!refreshed) {
                try {
                    logger.debug(String.format("Get new token"));
                    onectaSignInClient.fetchAccessToken();
                    response = doBearerRequestGet(true);
                } catch (DaikinCommunicationException ex) {
                    throw new DaikinCommunicationException(ex);
                }
            }
        }

        return response;
    }

    private static Response doBearerRequestPatch(String url, Object body) {
        return doBearerRequestPatch(url, body, false);
    }

    private static Response doBearerRequestPatch(String url, Object body, Boolean refreshed) {
        Response response = null;
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            response = OnectaConfiguration.getHttpClient().newRequest(url).method(HttpMethod.PATCH)
                    .content(new StringContentProvider(new Gson().toJson(body)), MediaType.APPLICATION_JSON)
                    .header(HttpHeader.AUTHORIZATION, String.format(HTTPHEADER_BEARER, onectaSignInClient.getToken()))
                    .header(HttpHeader.USER_AGENT, USER_AGENT_VALUE)
                    .header(HTTPHEADER_X_API_KEY, HTTPHEADER_X_API_KEY_VALUE).send();

            logger.debug("Request : " + response.getRequest().getURI().toString());
            logger.debug("Body    : " + new Gson().toJson(body));
            logger.debug("Resonse : " + ((HttpContentResponse) response).getContentAsString());

            if (response.getStatus() == HttpStatus.UNAUTHORIZED_401 && !refreshed) {
                onectaSignInClient.fetchAccessToken();
                response = doBearerRequestPatch(url, body, true);
            }
            return response;
        } catch (Exception e) {
            if (!refreshed) {
                try {
                    logger.debug(String.format("Get new token"));
                    onectaSignInClient.fetchAccessToken();
                    response = doBearerRequestPatch(url, body, true);
                } catch (DaikinCommunicationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return response;
    }

    public static void refreshUnitsData(Thing bridgeThing) throws DaikinCommunicationException {
        Response response = null;
        String jsonString = "";
        boolean dataAvailable = false;
        Boolean logRawData = bridgeThing.getConfiguration().get(CHANNEL_LOGRAWDATA).toString().equals("true");
        String stubDataFile = bridgeThing.getConfiguration().get(CHANNEL_STUBDATAFILE) == null ? ""
                : bridgeThing.getConfiguration().get(CHANNEL_STUBDATAFILE).toString();

        if (stubDataFile.isEmpty()) {
            response = doBearerRequestGet(false);
            if (logRawData) {
                logger.info(((HttpContentResponse) response).getContentAsString());
            }
            dataAvailable = (response.getStatus() == HttpStatus.OK_200);
            jsonString = JsonParser.parseString(((HttpContentResponse) response).getContentAsString()).toString();
        } else {
            try {
                jsonString = new String(Files.readAllBytes(Paths.get(stubDataFile)), StandardCharsets.UTF_8);
                dataAvailable = true;
            } catch (IOException e) {
                logger.debug("Error reading file :" + e.getMessage());
            }
        }

        if (dataAvailable) {
            rawData = JsonParser.parseString(jsonString).getAsJsonArray();
            onectaData.getAll().clear();
            for (int i = 0; i < rawData.size(); i++) {
                onectaData.getAll()
                        .add(Objects.requireNonNull(new Gson().fromJson(rawData.get(i).getAsJsonObject(), Unit.class)));
            }
        } else {
            throw new DaikinCommunicationForbiddenException(
                    String.format("GetToken resonse (%s) : (%s)", response.getStatus(), jsonString));
        }
    }

    public static Unit getUnit(String unitId) {
        return onectaData.findById(unitId);
    }

    public static JsonObject getRawData(String unitId) {
        JsonObject jsonObject = null;
        for (int i = 0; i < rawData.size(); i++) {
            jsonObject = rawData.get(i).getAsJsonObject();
            if (jsonObject.get("id").getAsString().equals(unitId)) {
                return jsonObject;
            }
        }

        return new JsonObject();
    }

    public static void setPowerOnOff(String unitId, String managementPointType, Enums.OnOff value) {
        logger.debug(String.format("setPowerOnOff : %s, %s, %s", unitId, managementPointType, value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getUrlOnOffTest(unitId, managementPointType), commandOnOf);
    }

    public static void setPowerOnOff(String unitId, Enums.ManagementPoint managementPointType, Enums.OnOff value) {
        logger.debug(String.format("setPowerOnOff : %s, %s, %s", unitId, managementPointType.getValue(), value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getUrlOnOff(unitId, managementPointType), commandOnOf);
    }

    public static void setPowerFulModeOnOff(String unitId, Enums.ManagementPoint managementPointType,
            Enums.OnOff value) {
        logger.debug(String.format("setPowerFulModeOnOff : %s, %s, %s", unitId, managementPointType.getValue(), value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getUrlPowerFulModeOnOff(unitId, managementPointType), commandOnOf);
    }

    public static void setEconoMode(String unitId, Enums.ManagementPoint managementPointType, Enums.OnOff value) {
        logger.debug(String.format("setEconoMode: %s, %s, %s", unitId, managementPointType.getValue(), value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getEconoMode(unitId, managementPointType), commandOnOf);
    }

    public static void setCurrentOperationMode(String unitId, Enums.ManagementPoint managementPointType,
            Enums.OperationMode operationMode) {
        logger.debug(String.format("setCurrentOperationMode : %s, %s, %s", unitId, managementPointType.getValue(),
                operationMode.getValue()));
        doBearerRequestPatch(OnectaProperties.getOperationModeUrl(unitId, managementPointType),
                OnectaProperties.getOperationModeCommand(operationMode));
    }

    public static void setCurrentTemperatureRoomSet(String unitId, String embeddedId, Enums.OperationMode currentMode,
            float value) {
        logger.debug(
                String.format("setCurrentTemperatureRoomSet : %s, %s, %s", unitId, embeddedId, currentMode.getValue()));
        doBearerRequestPatch(OnectaProperties.getTemperatureControlUrl(unitId, embeddedId),
                OnectaProperties.getTemperatureRoomControlCommand(value, currentMode));
    }

    public static void setCurrentTemperatureHotWaterSet(String unitId, String embeddedId,
            Enums.OperationMode currentMode, float value) {
        logger.debug(String.format("setCurrentTemperatureHotWaterSet : %s, %s, %s", unitId, embeddedId,
                currentMode.getValue()));
        doBearerRequestPatch(OnectaProperties.getTemperatureControlUrl(unitId, embeddedId),
                OnectaProperties.getTemperatureHotWaterControlCommand(value, currentMode));
    }

    public static void setFanSpeed(String unitId, String embeddedId, Enums.OperationMode currentMode,
            Enums.FanSpeed fanspeed) {
        logger.debug(String.format("setFanSpeed : %s, %s, %s", unitId, embeddedId, currentMode.getValue()));
        doBearerRequestPatch(OnectaProperties.getTFanControlUrl(unitId, embeddedId),
                getTFanSpeedCurrentCommand(currentMode, fanspeed));
        if (fanspeed.getValueMode().equals(Enums.FanSpeedMode.FIXED.getValue())) {
            doBearerRequestPatch(OnectaProperties.getTFanControlUrl(unitId, embeddedId),
                    OnectaProperties.getTFanSpeedFixedCommand(currentMode, fanspeed));
        }
    }

    public static void setCurrentFanDirection(String unitId, String embeddedId, Enums.OperationMode currentMode,
            Enums.FanMovement fanMovement) {
        logger.debug(String.format("setCurrentFanDirection : %s, %s, %s", unitId, embeddedId, currentMode.getValue()));
        String url = getTFanControlUrl(unitId, embeddedId);
        switch (fanMovement) {
            case STOPPED:
                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionHorCommand(currentMode, Enums.FanMovementHor.STOPPED));

                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionVerCommand(currentMode, Enums.FanMovementVer.STOPPED));
                break;
            case VERTICAL:
                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionHorCommand(currentMode, Enums.FanMovementHor.STOPPED));

                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionVerCommand(currentMode, Enums.FanMovementVer.SWING));
                break;
            case HORIZONTAL:
                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionHorCommand(currentMode, Enums.FanMovementHor.SWING));

                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionVerCommand(currentMode, Enums.FanMovementVer.STOPPED));
                break;
            case VERTICAL_AND_HORIZONTAL:
                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionHorCommand(currentMode, Enums.FanMovementHor.SWING));

                doBearerRequestPatch(url,
                        OnectaProperties.getTFanDirectionVerCommand(currentMode, Enums.FanMovementVer.SWING));
                break;
            default:
                break;
        }
    }

    public static void setCurrentFanDirectionHor(String unitId, String embeddedId, Enums.OperationMode currentMode,
            Enums.FanMovementHor fanMovement) {
        logger.debug(
                String.format("setCurrentFanDirectionHor : %s, %s, %s", unitId, embeddedId, currentMode.getValue()));
        String url = getTFanControlUrl(unitId, embeddedId);
        doBearerRequestPatch(url, OnectaProperties.getTFanDirectionHorCommand(currentMode, fanMovement));
    }

    public static void setCurrentFanDirectionVer(String unitId, String embeddedId, Enums.OperationMode currentMode,
            Enums.FanMovementVer fanMovement) {
        logger.debug(
                String.format("setCurrentFanDirectionVer : %s, %s, %s", unitId, embeddedId, currentMode.getValue()));
        String url = getTFanControlUrl(unitId, embeddedId);
        doBearerRequestPatch(url, OnectaProperties.getTFanDirectionVerCommand(currentMode, fanMovement));
    }

    public static void setStreamerMode(String unitId, String embeddedId, Enums.OnOff value) {
        logger.debug(String.format("setStreamerMode: %s, %s, %s", unitId, embeddedId, value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getStreamerMode(unitId, embeddedId), commandOnOf);
    }

    public static void setHolidayMode(String unitId, String embeddedId, Enums.OnOff value) {
        logger.debug(String.format("setHolidayMode: %s, %s, %s", unitId, embeddedId, value));
        CommandTrueFalse commandTrueFalse = new CommandTrueFalse(value);
        doBearerRequestPatch(getHolidayMode(unitId, embeddedId), commandTrueFalse);
    }

    public static void setDemandControl(String unitId, String embeddedId, Enums.DemandControl value) {
        logger.debug(String.format("setDemandControl: %s, %s, %s", unitId, embeddedId, value));
        doBearerRequestPatch(getTDemandControlUrl(unitId, embeddedId),
                OnectaProperties.getTDemandControlCommand(value));
    }

    public static void setDemandControlFixedValue(String unitId, String embeddedId, Integer value) {
        logger.debug(String.format("setDemandControlFixedValue: %s, %s, %s", unitId, embeddedId, value));

        doBearerRequestPatch(getTDemandControlUrl(unitId, embeddedId),
                OnectaProperties.getTDemandControlFixedValueCommand(value));
    }

    public static String getRefreshToken() {
        return onectaSignInClient.getRefreshToken();
    }

    public static void setRefreshToken(String refreshToken) {
        onectaSignInClient.setRefreshToken(refreshToken);
    }

    public static void setTargetTemperatur(String unitId, String embeddedId, Float value) {
        logger.debug(String.format("setRefreshToken: %s, %s, %s", unitId, embeddedId, value));
        doBearerRequestPatch(getTargetTemperaturUrl(unitId, embeddedId), getTargetTemperaturCommand(value));
    }

    public static void setSetpointLeavingWaterOffset(String unitId, String embeddedId,
            Enums.OperationMode operationMode, Float value) {
        logger.debug(String.format("setRefreshToken: %s, %s, %s, %s", unitId, embeddedId, operationMode, value));
        doBearerRequestPatch(OnectaProperties.getTemperatureControlUrl(unitId, embeddedId),
                OnectaProperties.getSetpointLeavingWaterOffsetCommand(value, operationMode));
    }

    public static void setSetpointLeavingWaterTemperature(String unitId, String embeddedId,
            Enums.OperationMode operationMode, Float value) {
        logger.debug(String.format("setRefreshToken: %s, %s, %s, %s", unitId, embeddedId, operationMode, value));
        doBearerRequestPatch(OnectaProperties.getTemperatureControlUrl(unitId, embeddedId),
                OnectaProperties.getSetpointLeavingWaterTemperatureCommand(value, operationMode));
    }
}
