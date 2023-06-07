package org.openhab.binding.onecta.internal.api.dto.units;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionTypes

{
    @SerializedName("__type")
    private Mode currentMode;
    @SerializedName("__type")
    private ActionTypesModes modes;
}
