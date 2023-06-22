package org.openhab.binding.onecta.internal.api;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;
import org.openhab.binding.onecta.internal.api.dto.units.Units;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationException;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationForbiddenException;
import org.openhab.core.io.net.http.HttpClientFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OnactaConnectionClient {
    private JsonArray rawData = new JsonArray();
    private Units onectaData = new Units();
    private HttpClient httpClient;

    private OnectaSignInClient onectaSignInClient;

    public OnactaConnectionClient(HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
        this.onectaSignInClient = new OnectaSignInClient(httpClientFactory);
    }

    public void startConnecton(String userId, String password) {
        onectaSignInClient.signIn(userId, password);
    }

    public Boolean isOnline() {
        return onectaSignInClient.isOnline();
    }

    private Response doBearerRequest(HttpMethod httpMethod, Boolean refreshed) {
        Response response;
        try {
            if (!onectaSignInClient.isOnline()) {
                onectaSignInClient.signIn();
            }

            response = httpClient.newRequest("https://api.prod.unicloud.edc.dknadmin.be/v1/gateway-devices")
                    .method(httpMethod)
                    .header(HttpHeader.AUTHORIZATION, String.format("Bearer %s", onectaSignInClient.getToken()))
                    .header(HttpHeader.USER_AGENT, "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0")
                    .header("x-api-key", "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC").send();

            if (response.getStatus() == HttpStatus.UNAUTHORIZED_401 && !refreshed) {
                onectaSignInClient.fetchAccessToken();
                doBearerRequest(httpMethod, true);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (DaikinCommunicationException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public Units getUnits() throws DaikinCommunicationException {
        Response response;

        response = doBearerRequest(HttpMethod.GET, false);
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

        return onectaData;
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
            ;
        }

        return new JsonObject();
    }

    public void setPowerOnOff(String unitId, String value) {
    }
}
