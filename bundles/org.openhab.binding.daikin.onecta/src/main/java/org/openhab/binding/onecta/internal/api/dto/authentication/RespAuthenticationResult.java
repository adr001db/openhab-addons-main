package org.openhab.binding.onecta.internal.api.dto.authentication;

import com.google.gson.annotations.SerializedName;

public class RespAuthenticationResult {
    @SerializedName("AccessToken")
    public String accessToken;
    @SerializedName("ExpiresIn")
    public int expiresIn;
    @SerializedName("IdToken")
    public String idToken;
    @SerializedName("TokenType")
    public String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
