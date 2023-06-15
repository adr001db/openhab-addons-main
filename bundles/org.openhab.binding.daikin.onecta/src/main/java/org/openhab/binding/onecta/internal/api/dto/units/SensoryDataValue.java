package org.openhab.binding.onecta.internal.api.dto.units;

import org.openhab.binding.onecta.internal.api.Enums;

import com.google.gson.annotations.SerializedName;

public class SensoryDataValue {
    @SerializedName("roomTemperature")
    private IconID roomTemperature;
    @SerializedName("roomHumidity")
    private IconID roomHumidity;
    @SerializedName("outdoorTemperature")
    private IconID outdoorTemperature;

    public IconID getRoomTemperature() {
        return roomTemperature;
    }

    public IconID getRoomHumidity() {
        return roomHumidity;
    }

    public IconID getOutdoorTemperature() {
        return outdoorTemperature;
    }

    public IconID getSensorData(Enums.SensorData sensorData) {

        if (sensorData.getValue() == Enums.SensorData.ROOMTEMP.getValue()) {
            return this.roomTemperature;
        } else if (sensorData.getValue() == Enums.SensorData.ROOMHUMINITY.getValue()) {
            return this.roomHumidity;
        } else if (sensorData.getValue() == Enums.SensorData.OUTDOORTEMP.getValue()) {
            return this.outdoorTemperature;
        } else
            return null;
    }
}
