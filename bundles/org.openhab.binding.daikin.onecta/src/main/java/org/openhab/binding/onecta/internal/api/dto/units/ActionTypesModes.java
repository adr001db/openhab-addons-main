package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class ActionTypesModes {
    @SerializedName("fixed")
    private IconID fixed;

    public IconID getFixed() {
        return fixed;
    }
}
