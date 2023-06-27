package org.openhab.binding.onecta.internal.api.dto.commands;

import com.google.gson.annotations.SerializedName;

public class CommandOnOf {
    @SerializedName("value")
    public String value;

    @SerializedName("path")
    public String path;

    public CommandOnOf(String value) {
        this.value = value.toLowerCase();
    }

    public CommandOnOf(String value, String path) {
        this.value = value.toLowerCase();
        this.path = path;
    }
}
