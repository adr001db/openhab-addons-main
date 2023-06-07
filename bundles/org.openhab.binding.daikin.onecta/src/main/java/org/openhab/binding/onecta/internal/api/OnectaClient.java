package org.openhab.binding.onecta.internal.api;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.onecta.internal.api.dto.authentication.ReqAuthenticationRoot;
import org.openhab.binding.onecta.internal.api.dto.authentication.RespAuthenticationRoot;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;
import org.openhab.binding.onecta.internal.api.dto.units.Units;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@NonNullByDefault
public class OnectaClient {
    private final Logger logger = LoggerFactory.getLogger(OnectaClient.class);

    private static final String API_HOST = "https://jbv1-api.emotorwerks.com/";
    private static final String API_ACCOUNT = API_HOST + "box_pin";
    private static final String API_DEVICE = API_HOST + "box_api_secure";

    private String refreshToken = "";
    private String clientId = "";

    private Units onectaData = new Units();
    private HttpClient httpClient;

    private RespAuthenticationRoot respAuthenticationRoot = new RespAuthenticationRoot();

    public OnectaClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void fetchAccessToken() {

        respAuthenticationRoot = new RespAuthenticationRoot();
        ReqAuthenticationRoot reqAuthenticationRoot = new ReqAuthenticationRoot(this.clientId, this.refreshToken);

        Request request = httpClient.POST("https://cognito-idp.eu-west-1.amazonaws.com");

        request.header("x-amz-target", "AWSCognitoIdentityProviderService.InitiateAuth");
        request.header("Content-Type", "application/x-amz-json-1.1");
        request.content(
                new StringContentProvider(new Gson().toJson(reqAuthenticationRoot, ReqAuthenticationRoot.class)),
                "application/json");

        ContentResponse response = null;
        try {
            response = request.send();
            JsonObject jsonResponse = JsonParser.parseString(response.getContentAsString()).getAsJsonObject();
            respAuthenticationRoot = Objects
                    .requireNonNull(new Gson().fromJson(jsonResponse.getAsJsonObject(), RespAuthenticationRoot.class));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (!isConnected()) {
            logger.warn("Not connected to Daikin Onecta: Type '{}' - Message '{}' .",
                    respAuthenticationRoot.get__type(), respAuthenticationRoot.getMessage());
        }
    }

    public Units fetchOnectaData() {

        Response response;
        try {
            response = httpClient.newRequest("https://api.prod.unicloud.edc.dknadmin.be/v1/gateway-devices")
                    .method(HttpMethod.GET)
                    .header(HttpHeader.AUTHORIZATION,
                            String.format("%s %s", respAuthenticationRoot.getAuthenticationResult().getTokenType(),
                                    respAuthenticationRoot.getAuthenticationResult().getAccessToken()))
                    .header(HttpHeader.USER_AGENT, "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0")
                    .header("x-api-key", "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC").send();

            JsonArray jsonArray = JsonParser.parseString(((HttpContentResponse) response).getContentAsString())
                    .getAsJsonArray();
            onectaData.getUnits().clear();
            for (int i = 0; i < jsonArray.size(); i++) {
                onectaData.getUnits().add(
                        Objects.requireNonNull(new Gson().fromJson(jsonArray.get(i).getAsJsonObject(), Unit.class)));
            }

            return onectaData;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean isConnected() {
        return !respAuthenticationRoot.getAuthenticationResult().getAccessToken().isBlank();
    }

    public Units getOnectaData() {
        return onectaData;
    }
}
