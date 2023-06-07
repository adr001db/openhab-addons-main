package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class IconID {
    @SerializedName("settable")
    private boolean settable;
    @SerializedName("value")
    private Double value;
    @SerializedName("maxValue")
    private Double maxValue;
    @SerializedName("minValue")
    private Double minValue;
    @SerializedName("stepValue")
    private Double stepValue;
    @SerializedName("unit")
    private String unit;

    public boolean isSettable() {
        return settable;
    }

    public Double getValue() {
        return value;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getStepValue() {
        return stepValue;
    }

    public String getUnit() {
        return unit;
    }
}
