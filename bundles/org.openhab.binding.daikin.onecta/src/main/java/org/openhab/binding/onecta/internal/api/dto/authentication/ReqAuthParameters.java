package org.openhab.binding.onecta.internal.api.dto.authentication;

import com.google.gson.annotations.SerializedName;

public class ReqAuthParameters {
    @SerializedName("REFRESH_TOKEN")
    public String refreshToken;

    public ReqAuthParameters(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
