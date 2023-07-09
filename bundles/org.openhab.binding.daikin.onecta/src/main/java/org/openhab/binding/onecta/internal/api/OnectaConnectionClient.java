package org.openhab.binding.onecta.internal.api;

import static org.openhab.binding.onecta.internal.api.OnectaProperties.*;

import java.util.Objects;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandOnOf;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandString;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandTrueFalse;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;
import org.openhab.binding.onecta.internal.api.dto.units.Units;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationException;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationForbiddenException;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OnectaConnectionClient {

    private final Logger logger = LoggerFactory.getLogger(OnectaSignInClient.class);
    private JsonArray rawData = new JsonArray();
    private Units onectaData = new Units();

    public Units getUnits() {
        return onectaData;
    }

    private HttpClient httpClient;

    private OnectaSignInClient onectaSignInClient;

    public OnectaConnectionClient(HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
        this.onectaSignInClient = new OnectaSignInClient(httpClientFactory);
    }

    public void startConnecton(String userId, String password) {
        onectaSignInClient.signIn(userId, password);
    }

    public Boolean isOnline() {
        return onectaSignInClient.isOnline();
    }

    private Response doBearerRequestGet(Boolean refreshed) {
        Response response = null;
        logger.debug(String.format("doBearerRequestGet : refershed %s", refreshed.toString()));
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            response = httpClient.newRequest(OnectaProperties.getBaseUrl("")).method(HttpMethod.GET)
                    .header(HttpHeader.AUTHORIZATION, String.format("Bearer %s", onectaSignInClient.getToken()))
                    .header(HttpHeader.USER_AGENT, "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0")
                    .header("x-api-key", "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC").send();

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
                    throw new RuntimeException(ex);
                }
            }
        }

        return response;
    }

    private Response doBearerRequestPatch(String url, Object body) {
        return doBearerRequestPatch(url, body, false);
    }

    private Response doBearerRequestPatch(String url, Object body, Boolean refreshed) {
        Response response = null;
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            response = httpClient.newRequest(url).method(HttpMethod.PATCH)
                    .content(new StringContentProvider(new Gson().toJson(body)), "application/json")
                    .header(HttpHeader.AUTHORIZATION, String.format("Bearer %s", onectaSignInClient.getToken()))
                    .header(HttpHeader.USER_AGENT, "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0")
                    .header("x-api-key", "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC").send();

            if (response.getStatus() == HttpStatus.UNAUTHORIZED_401 && !refreshed) {
                onectaSignInClient.fetchAccessToken();
                doBearerRequestPatch(url, body, true);
            }
            return response;
        } catch (Exception e) {
            if (!refreshed) {
                try {
                    logger.debug(String.format("Get new token"));
                    onectaSignInClient.fetchAccessToken();
                    response = doBearerRequestGet(true);
                } catch (DaikinCommunicationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return response;
    }

    public void refreshUnitsData() throws DaikinCommunicationException {
        Response response;

        response = doBearerRequestGet(false);
        String jsonString = JsonParser.parseString(((HttpContentResponse) response).getContentAsString()).toString();
        if (response.getStatus() == HttpStatus.OK_200) {
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

    public Unit getUnit(String unitId) {
        return onectaData.findById(unitId);
    }

    public JsonObject getRawData(String unitId) {
        JsonObject jsonObject = null;
        for (int i = 0; i < rawData.size(); i++) {
            jsonObject = rawData.get(i).getAsJsonObject();
            if (jsonObject.get("id").getAsString().equals(unitId)) {
                return jsonObject;
            }
        }

        return new JsonObject();
    }

    public void setPowerOnOff(String unitId, Enums.OnOff value) {
        logger.debug(String.format("setPowerOnOff : %s, %s", unitId, value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getUrlOnOff(unitId), commandOnOf);
    }

    public void setEconoMode(String unitId, Enums.OnOff value) {
        logger.debug(String.format("setEconoMode: %s, %s", unitId, value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getEconoMode(unitId), commandOnOf);
    }

    public void setCurrentOperationMode(String unitId, Enums.OperationMode operationMode) {
        doBearerRequestPatch(OnectaProperties.getOperationModeUrl(unitId),
                OnectaProperties.getOperationModeCommand(operationMode));
    }

    public void setCurrentTemperatureSet(String unitId, Enums.OperationMode currentMode, float value) {
        doBearerRequestPatch(OnectaProperties.getTemperatureControlUrl(unitId),
                OnectaProperties.getTemperatureControlCommand(value, currentMode));
    }

    public void setFanSpeed(String unitId, Enums.OperationMode currentMode, Enums.FanSpeed fanspeed) {
        doBearerRequestPatch(OnectaProperties.getTFanControlUrl(unitId),
                getTFanSpeedCurrentCommand(currentMode, fanspeed));
        if (fanspeed.getValueMode().equals(Enums.FanSpeedMode.FIXED.getValue())) {
            doBearerRequestPatch(OnectaProperties.getTFanControlUrl(unitId),
                    OnectaProperties.getTFanSpeedFixedCommand(currentMode, fanspeed));
        }
    }

    public void setCurrentFanDirection(String unitId, Enums.OperationMode currentMode, Enums.FanMovement fanMovement) {
        String url = getTFanControlUrl(unitId);
        CommandString commandString;
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

    public void setStreamerMode(String unitId, Enums.OnOff value) {
        logger.debug(String.format("setStreamerMode: %s, %s", unitId, value));
        CommandOnOf commandOnOf = new CommandOnOf(value);
        doBearerRequestPatch(getStreamerMode(unitId), commandOnOf);
    }

    public void setHolidayMode(String unitId, Enums.OnOff value) {
        logger.debug(String.format("setHolidayMode: %s, %s", unitId, value));
        CommandTrueFalse commandTrueFalse = new CommandTrueFalse(value);
        doBearerRequestPatch(getHolidayMode(unitId), commandTrueFalse);
    }

    public void setDemandControl(String unitId, Enums.DemandControl value) {
        logger.debug(String.format("setDemandControl: %s, %s", unitId, value));
        doBearerRequestPatch(getTDemandControlUrl(unitId), OnectaProperties.getTDemandControlCommand(value));
    }
}
