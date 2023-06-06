package org.openhab.binding.onecta.internal.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.openhab.binding.onecta.internal.api.dto.authentication.ReqAuthenticationRoot;
import org.openhab.binding.onecta.internal.api.dto.authentication.RespAuthenticationRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
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

    private HttpClient httpClient;

    private static RespAuthenticationRoot respAuthenticationRoot;

    public OnectaClient(HttpClient httpClient, String clientId, String refreshToken) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.refreshToken = refreshToken;
    }

    public void getAccessToken() {

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
            respAuthenticationRoot = (RespAuthenticationRoot) new Gson().fromJson(jsonResponse.getAsJsonObject(),
                    RespAuthenticationRoot.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        logger.info(respAuthenticationRoot.getAuthenticationResult().getAccessToken());
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
