package org.openhab.binding.onecta.internal.api;

public class OpenHabLocalClient {
    /*
     * private final Logger logger = LoggerFactory.getLogger(OnectaSignInClient.class);
     * 
     * private HttpClient httpClient;
     * private String LocalHostUrl;
     * private String apiToken;
     * 
     * 
     * public OpenHabLocalClient(HttpClientFactory httpClientFactory, String localHostUrl, String apiToken) {
     * this.httpClient = httpClientFactory.getCommonHttpClient();
     * this.apiToken = apiToken;
     * this.LocalHostUrl = localHostUrl;
     * }
     * 
     * public Response doBearerRequestGet(Boolean refreshed) throws DaikinCommunicationException {
     * Response response = null;
     * HttpClient httpClient = new HttpClient();
     * // logger.debug(String.format("doBearerRequestGet : refershed %s", refreshed.toString()));
     * try {
     * response = httpClient.newRequest(OnectaProperties.getBaseUrl("")).method(HttpMethod.GET)
     * .header(HttpHeader.AUTHORIZATION, String.format("Bearer %s", onectaSignInClient.getToken()))
     * .send();
     * } catch (Exception e) {
     * if (!refreshed) {
     * try {
     * logger.debug(String.format("Get new token"));
     * onectaSignInClient.fetchAccessToken();
     * response = doBearerRequestGet(true);
     * } catch (DaikinCommunicationException ex) {
     * throw new DaikinCommunicationException(ex);
     * }
     * }
     * }
     * 
     * return response;
     * }
     */
}
