package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class DemandControlValue {
    @SerializedName("currentMode")
    private GatwaySubValueString currentMode;
    // @SerializedName("modes")
    // private PurpleModes modes;

    public GatwaySubValueString getCurrentMode() {
        return currentMode;
    }
}
