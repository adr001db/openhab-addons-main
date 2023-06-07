package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class Ing {
    @SerializedName("d")
    private String[] day;
    @SerializedName("w")
    private String[] week;
    @SerializedName("m")
    private String[] month;

    public String[] getDay() {
        return day;
    }

    public String[] getWeek() {
        return week;
    }

    public String[] getMonth() {
        return month;
    }
}
