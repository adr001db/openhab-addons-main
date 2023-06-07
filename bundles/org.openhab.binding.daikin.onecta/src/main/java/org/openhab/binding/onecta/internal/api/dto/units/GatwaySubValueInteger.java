package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class GatwaySubValueInteger {
    @SerializedName("settable")
    private boolean settable;
    @SerializedName("value")
    private Integer value;
    @SerializedName("maxValue")
    private Integer maxValue;
    @SerializedName("minValue")
    private Integer minValue;
    @SerializedName("stepValue")
    private Integer stepValue;
}
