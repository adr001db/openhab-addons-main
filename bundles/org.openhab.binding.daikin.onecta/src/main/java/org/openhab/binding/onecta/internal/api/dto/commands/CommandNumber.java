package org.openhab.binding.onecta.internal.api.dto.commands;

import com.google.gson.annotations.SerializedName;

public class CommandNumber {
    @SerializedName("value")
    public float value;

    @SerializedName("path")
    public String path;

    public CommandNumber(float value) {
        this.value = value;
    }

    public CommandNumber(float value, String path) {
        this.value = value;
        this.path = path;
    }
}
