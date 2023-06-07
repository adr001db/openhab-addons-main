package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class DtoIsCloudConnectionUp {
    @SerializedName("settable")
    private boolean settable;
    @SerializedName("value")
    private boolean value;
}
