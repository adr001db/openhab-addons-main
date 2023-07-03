package org.openhab.binding.onecta.internal.api;

import java.util.Objects;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandFloat;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandInteger;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandOnOf;
import org.openhab.binding.onecta.internal.api.dto.commands.CommandString;
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
        // logger.info(String.format("doBearerRequestGet : refershed %s", refreshed.toString()));
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            String urlTot = "https://api.prod.unicloud.edc.dknadmin.be/v1/gateway-devices";
            response = httpClient.newRequest(urlTot).method(HttpMethod.GET)
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
                    logger.info(String.format("Get new token"));
                    onectaSignInClient.fetchAccessToken();
                    response = doBearerRequestGet(true);
                } catch (DaikinCommunicationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return response;
    }

    private Response doBearerRequestPatch(String url, Object body, Boolean refreshed) {
        Response response = null;
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }
            String urlTot = String.format("https://api.prod.unicloud.edc.dknadmin.be/v1/gateway-devices%s", url);
            response = httpClient.newRequest(urlTot).method(HttpMethod.PATCH)
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
                    logger.info(String.format("Get new token"));
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

    public void setPowerOnOff(String unitId, String value) {
        CommandOnOf commandOnOf = new CommandOnOf(value);
        String url = "/" + unitId + "/management-points/climateControl/characteristics/onOffMode";

        doBearerRequestPatch(url, commandOnOf, false);
    }

    public void setCurrentOperationMode(String unitId, Enums.OperationMode operationMode) {
        String url = "/" + unitId + "/management-points/climateControl/characteristics/operationMode";
        CommandString commandString = new CommandString(operationMode.getValue());

        doBearerRequestPatch(url, commandString, false);
    }

    public void setCurrentTemperatureSet(String unitId, Enums.OperationMode currentMode, float value) {
        String url = "/" + unitId + "/management-points/climateControl/characteristics/temperatureControl";
        CommandFloat commandFloat = new CommandFloat(value,
                String.format("/operationModes/%s/setpoints/roomTemperature", currentMode.getValue()));

        doBearerRequestPatch(url, commandFloat, false);
    }

    public void setFanSpeed(String unitId, Enums.OperationMode currentMode, Enums.FanSpeed fanspeed) {
        String url = "/" + unitId + "/management-points/climateControl/characteristics/fanControl";
        CommandString commandString = new CommandString(fanspeed.getValueMode(),
                String.format("/operationModes/%s/fanSpeed/currentMode", currentMode.getValue()));
        doBearerRequestPatch(url, commandString, false);

        if (fanspeed.getValueMode().equals(Enums.FanSpeedMode.FIXED.getValue())) {
            url = "/" + unitId + "/management-points/climateControl/characteristics/fanControl";
            CommandInteger commandInteger = new CommandInteger(fanspeed.getValueSpeed(),
                    String.format("/operationModes/%s/fanSpeed/modes/fixed", currentMode.getValue()));
            doBearerRequestPatch(url, commandInteger, false);
        }
    }

    public void setCurrentFanDirection(String unitId, Enums.OperationMode currentMode, Enums.FanMovement fanMovement) {
        String url = "/" + unitId + "/management-points/climateControl/characteristics/fanControl";
        CommandString commandString;
        switch (fanMovement) {
            case STOPPED:
                commandString = new CommandString(Enums.FanMovementHor.STOPPED.getValue(), String
                        .format("/operationModes/%s/fanDirection/horizontal/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);

                commandString = new CommandString(Enums.FanMovementVer.STOPPED.getValue(),
                        String.format("/operationModes/%s/fanDirection/vertical/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);
                break;
            case VERTICAL:
                commandString = new CommandString(Enums.FanMovementHor.STOPPED.getValue(), String
                        .format("/operationModes/%s/fanDirection/horizontal/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);

                commandString = new CommandString(Enums.FanMovementVer.SWING.getValue(),
                        String.format("/operationModes/%s/fanDirection/vertical/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);
                break;
            case HORIZONTAL:
                commandString = new CommandString(Enums.FanMovementHor.SWING.getValue(), String
                        .format("/operationModes/%s/fanDirection/horizontal/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);

                commandString = new CommandString(Enums.FanMovementVer.STOPPED.getValue(),
                        String.format("/operationModes/%s/fanDirection/vertical/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);
                break;
            case VERTICAL_AND_HORIZONTAL:
                commandString = new CommandString(Enums.FanMovementHor.SWING.getValue(), String
                        .format("/operationModes/%s/fanDirection/horizontal/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);

                commandString = new CommandString(Enums.FanMovementVer.SWING.getValue(),
                        String.format("/operationModes/%s/fanDirection/vertical/currentMode", currentMode.getValue()));
                doBearerRequestPatch(url, commandString, false);
                break;
            default:
                break;
        }
    }
}
