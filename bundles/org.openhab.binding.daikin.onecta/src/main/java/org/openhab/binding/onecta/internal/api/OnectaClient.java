package org.openhab.binding.onecta.internal.api;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.onecta.internal.api.dto.authentication.*;
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

@NonNullByDefault
public class OnectaClient {
    private final Logger logger = LoggerFactory.getLogger(OnectaClient.class);

    private static final String API_HOST = "https://jbv1-api.emotorwerks.com/";
    private static final String API_ACCOUNT = API_HOST + "box_pin";
    private static final String API_DEVICE = API_HOST + "box_api_secure";

    private final HttpClientFactory httpClientFactory;

    private String refreshToken = "";
    private String clientId = "7rk39602f0ds8lk0h076vvijnb";

    private String userId = "";
    private String password = "";

    private JsonArray rawData = new JsonArray();

    private Units onectaData = new Units();
    private HttpClient httpClient;

    private RespAuthenticationRoot respAuthenticationRoot = new RespAuthenticationRoot();

    public OnectaClient(HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
        this.httpClientFactory = httpClientFactory;
        // httpClient.setFollowRedirects(false);
    }

    public static String getSamlContext(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.split("=")[0].equals("samlContext")) {
                return param.split("=")[1];
            }
            String value = param.split("=")[1];

        }
        return "";
    }

    private String fetchRefreshToken() {

        final String daikinIssuer = "https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_SLI9qJpc7/.well-known/openid-configuration";
        final String daikinCloudUrl = "https://daikin-unicloud-prod.auth.eu-west-1.amazoncognito.com";
        final String apikey = "3_xRB3jaQ62bVjqXU1omaEsPDVYC0Twi1zfq1zHPu_5HFT0zWkDvZJS97Yw1loJnTm";
        final String apikey2 = "3_QebFXhxEWDc8JhJdBWmvUd1e0AaWJCISbqe4QIHrk_KzNVJFJ4xsJ2UZbl8OIIFY";
        final String openidClientId = "7rk39602f0ds8lk0h076vvijnb";
        final String daikinCloudVerion = "v1.3.5 - 02.06.2023";

        try {
            HttpClient httpClientLocal = httpClientFactory.getCommonHttpClient();
            httpClientLocal.setFollowRedirects(false);

            httpClientLocal.getCookieStore().removeAll();
            // Stap 1
            ContentResponse response = httpClientLocal.GET(daikinIssuer);

            JsonObject jsonResponse = JsonParser.parseString(response.getContentAsString()).getAsJsonObject();
            RespOpenidConfiruration respOpenidConfiruration = Objects
                    .requireNonNull(new Gson().fromJson(jsonResponse.getAsJsonObject(), RespOpenidConfiruration.class));

            String authEndpoint = respOpenidConfiruration.getAuthorizationEndpoint();
            String tokenEndpoint = respOpenidConfiruration.getTokenEndpoint();
            String saml2Endpoint = respOpenidConfiruration.getAuthorizationEndpoint().replace("/oauth2/authorize",
                    "/saml2/idpresponse");

            // Stap 2 create client Secret

            int length = 32;
            boolean useLetters = true;
            boolean useNumbers = true;
            String createdClientSecret = RandomStringUtils.random(length, useLetters, useNumbers);

            // Stap 3 create initial url
            final String redirectUri = "daikinunified%3A%2F%2Flogin";
            String url = authEndpoint
                    + String.format("?response_type=code&state=%s&client_id=%s&scope=openid&redirect_uri=%s",
                            createdClientSecret, openidClientId, redirectUri);
            // httpClientLocal.setFollowRedirects(false);
            response = httpClientLocal.GET(url);

            String redirectUrl = response.getHeaders().get(HttpHeader.LOCATION);
            logger.info("Redirect URL :" + redirectUrl);

            // List<HttpCookie> cookies = httpClientLocal.getCookieStore().getCookies();
            String cookieString = "";

            for (HttpCookie cookie : httpClientLocal.getCookieStore().getCookies()) {
                if (cookie.getName().equalsIgnoreCase("xsrf-token") || cookie.getName().equals("csrf-state")
                        || cookie.getName().equals("csrf-state-legacy")) {
                    cookieString += String.format("%s=%s; ", cookie.getName(), cookie.getValue());
                }
            }

            logger.info("cookies :" + cookieString);

            // Stap 4 Call the forward-url -> extract samlContext from request

            response = httpClientLocal.GET(redirectUrl);
            // response.getHeaders().get(HttpHeader.LOCATION)

            String samlContext = getSamlContext(response.getHeaders().get(HttpHeader.LOCATION).split("[?]")[1]);

            logger.info("samlContext :" + samlContext);

            // Stap 5 ### prepare request to get Api-Version
            httpClientLocal.setFollowRedirects(true);
            url = "https://cdns.gigya.com/js/gigya.js?apiKey=" + apikey;
            response = httpClientLocal.GET(url);

            Pattern pattern = Pattern.compile("(\\d+-\\d-\\d+)");
            Matcher matcher = pattern.matcher(response.getContentAsString());
            matcher.find();
            String apiVersion = matcher.group();

            // Stap 6 prepare request to get single-sign-on cookie
            url = String.format("https://cdc.daikin.eu/accounts.webSdkBootstrap?apiKey=%s&sdk=js_latest&format=json",
                    apikey);
            response = httpClientLocal.GET(url);

            String ssoCookieString = "";
            for (HttpCookie cookie : httpClientLocal.getCookieStore().getCookies()) {
                if (cookie.getName().equalsIgnoreCase("gmid") || cookie.getName().equalsIgnoreCase("ucid")
                        || cookie.getName().equalsIgnoreCase("hasGmid")) {
                    ssoCookieString += String.format("%s=%s; ", cookie.getName(), cookie.getValue());
                }
            }
            ssoCookieString += String.format("gig_bootstrap_%s=cdc_ver4; ", apikey);
            ssoCookieString += String.format("gig_canary_%s=false; ", apikey2);
            ssoCookieString += String.format("gig_canary_ver_%s=%s; ", apikey2, apiVersion);
            ssoCookieString += String.format("apiDomain_%s==cdc.daikin.eu; ", apikey2);

            response = httpClientLocal.newRequest("https://cdc.daikin.eu/accounts.login").method(HttpMethod.POST)
                    .header("content-type", "application/x-www-form-urlencoded").header("cookie", ssoCookieString)
                    .param("loginID", userId).param("password", password).param("sessionExpiration", "31536000")
                    .param("targetEnv", "jssdk").param("include", "profile,").param("loginMode", "standard")
                    .param("riskContext", "{\"b0\":7527,\"b2\":4,\"b5\":1").param("APIKey", apikey)
                    .param("sdk", "js_latest").param("authMode", "cookie")
                    .param("pageURL",
                            "https://my.daikin.eu/content/daikinid-cdc-saml/en/login.html?samlContext=" + samlContext)
                    .param("sdkBuild", "12208").param("format", "json").send();

            // stap 7 extract login-token
            jsonResponse = JsonParser.parseString(response.getContentAsString()).getAsJsonObject();
            RespStep7 respStep7 = Objects
                    .requireNonNull(new Gson().fromJson(jsonResponse.getAsJsonObject(), RespStep7.class));

            String loginToken = respStep7.getSessionInfo().getLogin_token();

            // stap 8 expand single-sign-on cookies with login-token
            String tijd = String.valueOf(new Date().getTime() + 3600000);
            ssoCookieString += String.format("glt_%s=%s; ", apikey, loginToken);
            ssoCookieString += String.format("gig_loginToken_%s=%s; ", apikey2, loginToken);
            ssoCookieString += String.format("gig_loginToken_%s_exp=%s; ", apikey2, tijd);
            ssoCookieString += String.format("gig_loginToken_%s_visited=%s; ", apikey2, "%2C" + apikey);

            url = String.format("https://cdc.daikin.eu/saml/v2.0/%s/idp/sso/continue", apikey);

            response = httpClientLocal.newRequest(url).method(HttpMethod.POST).header("cookie", ssoCookieString)
                    .param("samlContext", samlContext).param("loginToken", loginToken).send();

            pattern = Pattern.compile("name=\"SAMLResponse\" value=\"([^\"]+)");
            matcher = pattern.matcher(response.getContentAsString());
            matcher.find();
            String samlResponse = matcher.group().split("value=\"")[1];

            pattern = Pattern.compile("name=\"RelayState\" value=\"([^\"]+)");
            matcher = pattern.matcher(response.getContentAsString());
            matcher.find();
            String relayState = matcher.group().split("value=\"")[1];

            httpClientLocal.setFollowRedirects(false);
            // httpClientLocal.setCookieStore();
            httpClientLocal.setRequestBufferSize(20000);
            String bodyString = String.format("{\"SAMLResponse\": \"%s\", \"RelayState\": \"%s\"}", samlResponse,
                    relayState);
            response = httpClientLocal.newRequest(saml2Endpoint).method(HttpMethod.POST)
                    .header("content-type", "application/x-www-form-urlencoded").header("cookie", cookieString)
                    // .content(new StringContentProvider(bodyString))
                    // .param("samlContext", samlContext)
                    .param("SAMLResponse", samlResponse).param("RelayState", relayState).send();

            String daikinUnified = response.getHeaders().get(HttpHeader.LOCATION).split("code=")[1];
            httpClientLocal.setFollowRedirects(true);

            url = tokenEndpoint + "?grant_type=authorization_code&code=" + daikinUnified + "&state="
                    + createdClientSecret + "&client_id=" + openidClientId
                    + "&redirect_uri=daikinunified%3A%2F%2Flogin";
            response = httpClientLocal.newRequest(url).method(HttpMethod.POST)
                    .header("content-type", "application/x-www-form-urlencoded").header("cookie", cookieString).send();

            jsonResponse = JsonParser.parseString(response.getContentAsString()).getAsJsonObject();
            RespTokenResult respTokenResult = Objects
                    .requireNonNull(new Gson().fromJson(jsonResponse.getAsJsonObject(), RespTokenResult.class));

            return respTokenResult.getRefreshToken();

        } catch (InterruptedException e) {
            logger.warn(" InterruptedException Stuk1");
        } catch (ExecutionException e) {
            logger.warn(" ExecutionException Stuk1");
        } catch (TimeoutException e) {
            logger.warn(" TimeoutException Stuk1");
        }
        return "sdasdasdasd";
    }

    public void fetchAccessToken() throws DaikinCommunicationException {

        if (refreshToken.equals("")) {
            this.refreshToken = fetchRefreshToken();
        }

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
            logger.warn("Not connected to Daikin Onecta: Type '{}' - Message '{}' .",
                    respAuthenticationRoot.get__type(), respAuthenticationRoot.getMessage());
            throw new DaikinCommunicationException(e);
        } catch (TimeoutException e) {
            logger.warn("Not connected to Daikin Onecta: Type '{}' - Message '{}' .",
                    respAuthenticationRoot.get__type(), respAuthenticationRoot.getMessage());
            throw new DaikinCommunicationException(e);
        } catch (ExecutionException e) {
            logger.warn("Not connected to Daikin Onecta: Type '{}' - Message '{}' .",
                    respAuthenticationRoot.get__type(), respAuthenticationRoot.getMessage());
            throw new DaikinCommunicationException(e);
        }
    }

    private Units fetchOnectaData() throws DaikinCommunicationException {

        Response response;
        try {
            response = httpClient.newRequest("https://api.prod.unicloud.edc.dknadmin.be/v1/gateway-devices")
                    .method(HttpMethod.GET)
                    .header(HttpHeader.AUTHORIZATION,
                            String.format("Bearer %s",
                                    respAuthenticationRoot.getAuthenticationResult().getAccessToken()))
                    .header(HttpHeader.USER_AGENT, "Daikin/1.6.1.4681 CFNetwork/1209 Darwin/20.2.0")
                    .header("x-api-key", "xw6gvOtBHq5b1pyceadRp6rujSNSZdjx2AqT03iC").send();

            String jsonString = JsonParser.parseString(((HttpContentResponse) response).getContentAsString())
                    .toString();
            if (response.getStatus() == HttpStatus.OK_200) {
                rawData = JsonParser.parseString(jsonString).getAsJsonArray();
                onectaData.getAll().clear();
                for (int i = 0; i < rawData.size(); i++) {
                    onectaData.getAll().add(
                            Objects.requireNonNull(new Gson().fromJson(rawData.get(i).getAsJsonObject(), Unit.class)));
                }
            } else {
                throw new DaikinCommunicationForbiddenException(
                        String.format("GetToken resonse (%s) : (%s)", response.getStatus(), jsonString));
            }

            return onectaData;
        } catch (ExecutionException e) {
            throw new DaikinCommunicationForbiddenException(e);
        } catch (InterruptedException e) {
            throw new DaikinCommunicationException(e);
        } catch (TimeoutException e) {
            throw new DaikinCommunicationException(e);
        }
    }

    public Units getUnits() throws DaikinCommunicationException {
        try {
            return fetchOnectaData();
        } catch (DaikinCommunicationForbiddenException e) {
            fetchAccessToken();
            return fetchOnectaData();
        }
    }

    public Units getOnectaData() {
        return onectaData;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRefreshTokenToEmpty() {
        this.refreshToken = "";
    }
}
