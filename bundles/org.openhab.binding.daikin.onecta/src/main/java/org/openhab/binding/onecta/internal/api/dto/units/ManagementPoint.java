package org.openhab.binding.onecta.internal.api.dto.units;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagementPoint {
    private String embeddedID;
    private String managementPointType;
    private String managementPointCategory;
    private IsCloudConnectionUp daylightSavingTimeEnabled;
    private EEPROMVersion errorCode;
    private EEPROMVersion firmwareVersion;
    private IsCloudConnectionUp isFirmwareUpdateSupported;
    private IsCloudConnectionUp isInErrorState;
    private IsCloudConnectionUp ledEnabled;
    private EEPROMVersion ipAddress;
    private EEPROMVersion macAddress;
    private EEPROMVersion modelInfo;
    private EEPROMVersion regionCode;
    private EEPROMVersion serialNumber;
    private EEPROMVersion ssid;
    private EEPROMVersion timeZone;
    private EEPROMVersion wifiConnectionSSID;
    private IconID wifiConnectionStrength;
    private String managementPointSubType;
    private ConsumptionData consumptionData;
    private DemandControl demandControl;
    private DryKeepSetting econoMode;
    private FanControl fanControl;
    private HolidayMode holidayMode;
    private IconID iconID;
    private IsCloudConnectionUp isCoolHeatMaster;
    private IsCloudConnectionUp isHolidayModeActive;
    private IsCloudConnectionUp isInCautionState;
    private IsCloudConnectionUp isInModeConflict;
    private IsCloudConnectionUp isInWarningState;
    private IsCloudConnectionUp isLockFunctionEnabled;
    private Name name;
    private DryKeepSetting onOffMode;
    private DryKeepSetting operationMode;
    private DryKeepSetting outdoorSilentMode;
    private DryKeepSetting powerfulMode;
    private IsCloudConnectionUp isPowerfulModeActive;
    private Schedule schedule;
    private SensoryData sensoryData;
    private DryKeepSetting streamerMode;
    private TemperatureControl temperatureControl;
    private EEPROMVersion softwareVersion;
    private EEPROMVersion eepromVersion;
    private DryKeepSetting dryKeepSetting;
}
