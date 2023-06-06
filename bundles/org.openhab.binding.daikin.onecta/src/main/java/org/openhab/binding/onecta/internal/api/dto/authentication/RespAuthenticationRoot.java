package org.openhab.binding.onecta.internal.api.dto.authentication;

import com.google.gson.annotations.SerializedName;

public class RespAuthenticationRoot {
    @SerializedName("AuthenticationResult")
    public RespAuthenticationResult respAuthenticationResult;
    @SerializedName("ChallengeParameters")
    public RespChallengeParameters respChallengeParameters;
    @SerializedName("__type")
    public String __type;
    @SerializedName("message")
    public String message;

    public RespAuthenticationResult getAuthenticationResult() {
        return respAuthenticationResult;
    }

    public RespChallengeParameters getChallengeParameters() {
        return respChallengeParameters;
    }

    public String get__type() {
        return __type;
    }

    public String getMessage() {
        return message;
    }
}
