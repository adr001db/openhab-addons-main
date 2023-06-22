package org.openhab.binding.onecta.internal.service;

import org.openhab.binding.onecta.internal.api.Enums;
import org.openhab.binding.onecta.internal.api.dto.units.ManagementPoint;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;

import com.google.gson.JsonObject;

public class DataTransportService {

    private Unit unit;
    private JsonObject rawData;

    public void setData(Unit unit, JsonObject rawData) {
        this.unit = unit;
        this.rawData = rawData;
    }

    public JsonObject getRawData() {
        return rawData;
    }

    public Boolean isAvailable() {
        return this.unit != null;
    }

    public ManagementPoint getManagementPoint(Enums.ManagementPoint managementPoint) {
        return unit.findManagementPointsById(managementPoint.getValue());
    }

    public Enums.OperationMode getcurrentOperationMode() {
        return Enums.OperationMode
                .fromValue(getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getOperationMode().getValue());
    }

    public Enums.FanSpeed getCurrentFanspeed() {
        String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                .getOperationModes().getFanOperationMode(getcurrentOperationMode()).getFanSpeed().getCurrentMode()
                .getValue();
        if (Enums.FanSpeedMode.FIXED.getValue().equals(fanMode)) {
            Integer fanSpeed = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getcurrentOperationMode()).getFanSpeed().getModes()
                    .getFixed().getValue();
            return Enums.FanSpeed.fromValue(String.format("%s_%s", fanMode, fanSpeed.toString()));
        }
        return Enums.FanSpeed.fromValue(fanMode);
    }

    public Enums.FanMovementHor getCurrentFanDirectionHor() {
        try {
            String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getcurrentOperationMode()).getFanDirection()
                    .getHorizontal().getCurrentMode().getValue();
            return Enums.FanMovementHor.fromValue(fanMode);
        } catch (Exception e) {
            return Enums.FanMovementHor.NOTAVAILABLE;
        }
    }

    public Enums.FanMovementVer getCurrentFanDirectionVer() {
        try {
            String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getcurrentOperationMode()).getFanDirection().getVertical()
                    .getCurrentMode().getValue();
            return Enums.FanMovementVer.fromValue(fanMode);
        } catch (Exception e) {
            return Enums.FanMovementVer.NOTAVAILABLE;
        }
    }

    public Enums.FanMovement getCurrentFanDirection() {
        try {
            String setting = String.format("%s_%s", getCurrentFanDirectionHor().toString(),
                    getCurrentFanDirectionVer().toString());
            switch (setting) {
                case "STOPPED_STOPPED":
                    return Enums.FanMovement.STOPPED;
                case "NOTAVAILABLE_STOPPED":
                    return Enums.FanMovement.STOPPED;
                case "SWING_STOPPED":
                    return Enums.FanMovement.HORIZONTAL;
                case "STOPPED_SWING":
                    return Enums.FanMovement.VERTICAL;
                case "NOTAVAILABLE_SWING":
                    return Enums.FanMovement.VERTICAL;
                case "SWING_SWING":
                    return Enums.FanMovement.VERTICAL_AND_HORIZONTAL;
                case "STOPPED_WINDNICE":
                    return Enums.FanMovement.VERTICAL_EXTRA;
                case "NOTAVAILABLE_WINDNICE":
                    return Enums.FanMovement.VERTICAL_EXTRA;
                case "SWING_WINDNICE":
                    return Enums.FanMovement.VERTICAL_AND_HORIZONTAL_EXTRA;
                default:
                    throw new IllegalArgumentException("Invalid day of the week: ");
            }
        } catch (Exception e) {
            return Enums.FanMovement.UNKNOWN;
        }
    }

    public String getPowerOnOff() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getOnOffMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setPowerOnOff(String value) {
    }

    public String getUnitName() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getName().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getCurrentTemperatureSet() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTemperatureControl().getValue()
                    .getOperationModes().getOperationMode(getcurrentOperationMode()).getSetpoints().getRoomTemperature()
                    .getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getIndoorTemperature() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getSensoryData().getValue()
                    .getRoomTemperature().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getIndoorHumidity() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getSensoryData().getValue()
                    .getRoomHumidity().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getOutdoorTemperature() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getSensoryData().getValue()
                    .getOutdoorTemperature().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Double getActiveTemperatureSetting() {
        return null;
    }
}
