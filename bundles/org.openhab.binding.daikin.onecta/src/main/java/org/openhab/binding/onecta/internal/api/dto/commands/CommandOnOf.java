package org.openhab.binding.onecta.internal.api.dto.commands;

import com.google.gson.annotations.SerializedName;

public class CommandOnOf {
    @SerializedName("value")
    public String value;

    public CommandOnOf(String value) {
        this.value = value.toLowerCase();
    }
}
