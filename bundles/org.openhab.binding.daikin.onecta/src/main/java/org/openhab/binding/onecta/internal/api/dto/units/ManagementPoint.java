package org.openhab.binding.onecta.internal.api.dto.units;

import com.google.gson.annotations.SerializedName;

public class ManagementPoint {
    @SerializedName("embeddedId")
    private String embeddedId;
    @SerializedName("managementPointType")
    private String managementPointType;
    @SerializedName("managementPointSubType")
    private String managementPointSubType;
    @SerializedName("managementPointCategory")
    private String managementPointCategory;
    @SerializedName("daylightSavingTimeEnabled")
    private GatwaySubValueBoolean daylightSavingTimeEnabled;
    @SerializedName("errorCode")
    private GatwaySubValueString errorCode;
    @SerializedName("firmwareVersion")
    private GatwaySubValueString firmwareVersion;
    @SerializedName("isFirmwareUpdateSupported")
    private GatwaySubValueBoolean isFirmwareUpdateSupported;
    @SerializedName("isInErrorState")
    private GatwaySubValueBoolean isInErrorState;
    @SerializedName("ledEnabled")
    private GatwaySubValueBoolean ledEnabled;
    @SerializedName("ipAddress")
    private GatwaySubValueString ipAddress;
    @SerializedName("macAddress")
    private GatwaySubValueString macAddress;
    @SerializedName("modelInfo")
    private GatwaySubValueString modelInfo;
    @SerializedName("regionCode")
    private GatwaySubValueString regionCode;
    @SerializedName("serialNumber")
    private GatwaySubValueString serialNumber;
    @SerializedName("ssid")
    private GatwaySubValueString ssid;
    @SerializedName("timeZone")
    private GatwaySubValueString timeZone;
    @SerializedName("wifiConnectionSSID")
    private GatwaySubValueString wifiConnectionSSID;
    @SerializedName("wifiConnectionStrength")
    private GatwaySubValueInteger wifiConnectionStrength;
    @SerializedName("consumptionData")
    private ConsumptionData consumptionData;

    @SerializedName("demandControl")
    private DemandControl demandControl;

    @SerializedName("econoMode")
    private GatwaySubValueString econoMode;

    @SerializedName("fanControl")
    private FanControl fanControl;

    @SerializedName("holidayMode")
    private HolidayMode holidayMode;

    // @SerializedName("_id")
    // private IconID iconID;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isCoolHeatMaster;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isHolidayModeActive;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isInCautionState;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isInModeConflict;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isInWarningState;
    //
    // @SerializedName("_id")
    // private DtoIsCloudConnectionUp isLockFunctionEnabled;
    //
    @SerializedName("name")
    private Name name;

    @SerializedName("onOffMode")
    private GatwaySubValueString onOffMode;

    @SerializedName("operationMode")
    private GatwaySubValueString operationMode;
    @SerializedName("targetTemperature")
    private IconID targetTemperature;

    @SerializedName("outdoorSilentMode")
    private GatwaySubValueString outdoorSilentMode;

    @SerializedName("powerfulMode")
    private GatwaySubValueString powerfulMode;

    @SerializedName("isPowerfulModeActive")
    private GatwaySubValueBoolean isPowerfulModeActive;

    // @SerializedName("_id")
    // private Schedule schedule;
    //
    @SerializedName("sensoryData")
    private SensoryData sensoryData;

    @SerializedName("streamerMode")
    private GatwaySubValueString streamerMode;

    @SerializedName("temperatureControl")
    private TemperatureControl temperatureControl;

    // @SerializedName("_id")
    // private GatwaySubValueString softwareVersion;
    //
    // @SerializedName("_id")
    // private GatwaySubValueString gatwaySubValue;
    //
    // @SerializedName("_id")
    // private DryKeepSetting dryKeepSetting;

    public String getEmbeddedId() {
        return embeddedId;
    }

    public String getManagementPointType() {
        return managementPointType;
    }

    public String getManagementPointSubType() {
        return managementPointSubType;
    }

    public String getManagementPointCategory() {
        return managementPointCategory;
    }

    public GatwaySubValueBoolean getDaylightSavingTimeEnabled() {
        return daylightSavingTimeEnabled;
    }

    public GatwaySubValueString getErrorCode() {
        return errorCode;
    }

    public GatwaySubValueString getFirmwareVersion() {
        return firmwareVersion;
    }

    public GatwaySubValueBoolean getIsFirmwareUpdateSupported() {
        return isFirmwareUpdateSupported;
    }

    public GatwaySubValueBoolean getIsInErrorState() {
        return isInErrorState;
    }

    public GatwaySubValueBoolean getIsLedEnabled() {
        return ledEnabled;
    }

    public GatwaySubValueString getIpAddress() {
        return ipAddress;
    }

    public GatwaySubValueString getMacAddress() {
        return macAddress;
    }

    public GatwaySubValueString getModelInfo() {
        return modelInfo;
    }

    public GatwaySubValueString getRegionCode() {
        return regionCode;
    }

    public GatwaySubValueString getSerialNumber() {
        return serialNumber;
    }

    public GatwaySubValueString getSsid() {
        return ssid;
    }

    public GatwaySubValueString getTimeZone() {
        return timeZone;
    }

    public GatwaySubValueString getWifiConnectionSSID() {
        return wifiConnectionSSID;
    }

    public GatwaySubValueInteger getWifiConnectionStrength() {
        return wifiConnectionStrength;
    }

    public ConsumptionData getConsumptionData() {
        return consumptionData;
    }

    public DemandControl getDemandControl() {
        return demandControl;
    }

    public HolidayMode getHolidayMode() {
        return holidayMode;
    }

    public Name getName() {
        return name;
    }

    public String getNameValue() {
        return name != null ? name.getValue() : "";
    }

    public GatwaySubValueString getOnOffMode() {
        return onOffMode;
    }

    public GatwaySubValueString getOperationMode() {
        return operationMode;
    }

    public IconID getTargetTemperature() {
        return targetTemperature;
    }

    public GatwaySubValueString getOutdoorSilentMode() {
        return outdoorSilentMode;
    }

    public GatwaySubValueString getPowerfulMode() {
        return powerfulMode;
    }

    public GatwaySubValueBoolean getIsPowerfulModeActive() {
        return isPowerfulModeActive;
    }

    public SensoryData getSensoryData() {
        return sensoryData;
    }

    public GatwaySubValueString getStreamerMode() {
        return streamerMode;
    }

    public TemperatureControl getTemperatureControl() {
        return temperatureControl;
    }

    public FanControl getFanControl() {
        return fanControl;
    }

    public GatwaySubValueString getEconoMode() {
        return econoMode;
    }
}
