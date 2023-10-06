package org.openhab.binding.onecta.internal.service;

import java.time.ZonedDateTime;

import org.openhab.binding.onecta.internal.api.Enums;
import org.openhab.binding.onecta.internal.api.OnectaConnectionClient;
import org.openhab.binding.onecta.internal.api.dto.units.ManagementPoint;
import org.openhab.binding.onecta.internal.api.dto.units.Unit;

import com.google.gson.JsonObject;

public class DataTransportService {

    private String unitId;
    private Enums.ManagementPoint managementPointType;
    private Unit unit;
    private JsonObject rawData;

    public DataTransportService(String unitId, Enums.ManagementPoint managementPointType) {
        this.unitId = unitId;
        this.managementPointType = managementPointType;
    }

    public void refreshUnit() {
        this.unit = OnectaConnectionClient.getUnit(unitId);
        this.rawData = OnectaConnectionClient.getRawData(unitId);
    }

    public JsonObject getRawData() {
        return rawData;
    }

    public Boolean isAvailable() {
        return this.unit != null;
    }

    public ManagementPoint getManagementPoint(Enums.ManagementPoint managementPoint) {
        return unit.findManagementPointsByType(managementPoint.getValue());
    }

    public Enums.OperationMode getCurrentOperationMode() {
        return Enums.OperationMode
                .fromValue(getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getOperationMode().getValue());
    }

    public void setCurrentOperationMode(Enums.OperationMode value) {
        OnectaConnectionClient.setCurrentOperationMode(unitId, value);
    }

    public Enums.FanSpeed getCurrentFanspeed() {
        String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                .getOperationModes().getFanOperationMode(getCurrentOperationMode()).getFanSpeed().getCurrentMode()
                .getValue();
        if (Enums.FanSpeedMode.FIXED.getValue().equals(fanMode)) {
            Integer fanSpeed = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getCurrentOperationMode()).getFanSpeed().getModes()
                    .getFixed().getValue();
            return Enums.FanSpeed.fromValue(String.format("%s_%s", fanMode, fanSpeed.toString()));
        }
        return Enums.FanSpeed.fromValue(fanMode);
    }

    public void setFanSpeed(Enums.FanSpeed value) {
        OnectaConnectionClient.setFanSpeed(unitId, getCurrentOperationMode(), value);
    }

    public Enums.FanMovementHor getCurrentFanDirectionHor() {
        try {
            String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getCurrentOperationMode()).getFanDirection()
                    .getHorizontal().getCurrentMode().getValue();
            return Enums.FanMovementHor.fromValue(fanMode);
        } catch (Exception e) {
            return Enums.FanMovementHor.NOTAVAILABLE;
        }
    }

    public Enums.FanMovementVer getCurrentFanDirectionVer() {
        try {
            String fanMode = getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getFanControl().getValue()
                    .getOperationModes().getFanOperationMode(getCurrentOperationMode()).getFanDirection().getVertical()
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

    public void setCurrentFanDirection(Enums.FanMovement value) {
        OnectaConnectionClient.setCurrentFanDirection(unitId, getCurrentOperationMode(), value);
    }

    public void setCurrentFanDirectionHor(Enums.FanMovementHor value) {
        OnectaConnectionClient.setCurrentFanDirectionHor(unitId, getCurrentOperationMode(), value);
    }

    public void setCurrentFanDirectionVer(Enums.FanMovementVer value) {
        OnectaConnectionClient.setCurrentFanDirectionVer(unitId, getCurrentOperationMode(), value);
    }

    public String getPowerOnOff() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getOnOffMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public String getPowerFulModeOnOff() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getPowerfulMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setPowerOnOff(Enums.OnOff value) {
        OnectaConnectionClient.setPowerOnOff(unitId, value);
    }

    public void setPowerFulModeOnOff(Enums.OnOff value) {
        OnectaConnectionClient.setPowerFulModeOnOff(unitId, value);
    }

    public String getEconoMode() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getEconoMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setEconoMode(Enums.OnOff value) {
        OnectaConnectionClient.setEconoMode(unitId, value);
    }

    public String getUnitName() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getNameValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getCurrentTemperatureSet() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTemperatureControl().getValue()
                    .getOperationModes().getOperationMode(getCurrentOperationMode()).getSetpoints().getRoomTemperature()
                    .getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setCurrentTemperatureSet(float value) {
        if (value <= getCurrentTemperatureSetMax().floatValue())
            OnectaConnectionClient.setCurrentTemperatureSet(unitId, getCurrentOperationMode(), value);
    }

    public Number getCurrentTemperatureSetMin() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTemperatureControl().getValue()
                    .getOperationModes().getOperationMode(getCurrentOperationMode()).getSetpoints().getRoomTemperature()
                    .getMinValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getCurrentTemperatureSetMax() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTemperatureControl().getValue()
                    .getOperationModes().getOperationMode(getCurrentOperationMode()).getSetpoints().getRoomTemperature()
                    .getMaxValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getCurrentTemperatureSetStep() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTemperatureControl().getValue()
                    .getOperationModes().getOperationMode(getCurrentOperationMode()).getSetpoints().getRoomTemperature()
                    .getStepValue();
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

    public Number getLeavingWaterTemperatur() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getSensoryData().getValue()
                    .getLeavingWaterTemperature().getValue();
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

    public ZonedDateTime getTimeStamp() {
        try {
            return ZonedDateTime.parse(unit.getTimestamp());
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

    public Number getTargetTemperatur() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTargetTemperature().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setTargetTemperatur(float value) {
        OnectaConnectionClient.setTargetTemperatur(unitId, value);
    }

    public Number getTargetTemperaturMax() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTargetTemperature().getMaxValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getTargetTemperaturMin() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTargetTemperature().getMinValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Number getTargetTemperaturStep() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getTargetTemperature().getStepValue();
        } catch (Exception e) {
            return null;
        }
    }

    public String getStreamerMode() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getStreamerMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setStreamerMode(Enums.OnOff value) {
        OnectaConnectionClient.setStreamerMode(unitId, value);
    }

    public String getHolidayMode() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getHolidayMode().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setHolidayMode(Enums.OnOff value) {
        OnectaConnectionClient.setHolidayMode(unitId, value);
    }

    public Enums.DemandControl getDemandControl() {
        try {
            return Enums.DemandControl.fromValue(getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL)
                    .getDemandControl().getValue().getCurrentMode().getValue());
        } catch (Exception e) {
            return null;
        }
    }

    public void setDemandControl(Enums.DemandControl value) {
        OnectaConnectionClient.setDemandControl(unitId, value);
    }

    public Integer getDemandControlFixedValue() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getDemandControl().getValue().getModes()
                    .getFixedValues().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getDemandControlFixedStepValue() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getDemandControl().getValue().getModes()
                    .getFixedValues().getStepValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getDemandControlFixedMinValue() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getDemandControl().getValue().getModes()
                    .getFixedValues().getMinValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getDemandControlFixedMaxValue() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getDemandControl().getValue().getModes()
                    .getFixedValues().getMaxValue();
        } catch (Exception e) {
            return null;
        }
    }

    public void setDemandControlFixedValue(Integer value) {
        OnectaConnectionClient.setDemandControlFixedValue(unitId, value);
    }

    public Float[] getConsumptionCoolingDay() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getCooling().getDay();
        } catch (Exception e) {
            return null;
        }
    }

    public Float[] getConsumptionCoolingWeek() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getCooling().getWeek();
        } catch (Exception e) {
            return null;
        }
    }

    public Float[] getConsumptionCoolingMonth() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getCooling().getMonth();
        } catch (Exception e) {
            return null;
        }
    }

    public Float[] getConsumptionHeatingDay() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getHeating().getDay();
        } catch (Exception e) {
            return null;
        }
    }

    public Float[] getConsumptionHeatingWeek() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getHeating().getWeek();
        } catch (Exception e) {
            return null;
        }
    }

    public Float[] getConsumptionHeatingMonth() {
        try {
            return getManagementPoint(Enums.ManagementPoint.CLIMATECONTROL).getConsumptionData().getValue()
                    .getElectrical().getHeating().getMonth();
        } catch (Exception e) {
            return null;
        }
    }

    /* GateWay data */

    public String getTimeZone() {
        try {
            return getManagementPoint(this.managementPointType).getTimeZone().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public String getWifiConectionSSid() {
        try {
            return getManagementPoint(this.managementPointType).getWifiConnectionSSID().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getWifiConectionStrength() {
        try {
            return getManagementPoint(this.managementPointType).getWifiConnectionStrength().getValue();
        } catch (Exception e) {
            return null;
        }
    }
}
