package org.openhab.binding.onecta.internal.api.dto.units;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class Unit {
    @SerializedName("_id")
    private UUID id;
    @SerializedName("id")
    private UUID initID;
    @SerializedName("type")
    private String type;
    @SerializedName("deviceModel")
    private String deviceModel;
    @SerializedName("isCloudConnectionUp")
    private GatwaySubValueBoolean IsCloudConnectionUp;
    @SerializedName("managementPoints")
    private ManagementPoint[] managementPoints;
    @SerializedName("embeddedId")
    private String embeddedID;
    @SerializedName("timestamp")
    private String timestamp;

    public UUID getId() {
        return id;
    }

    public UUID getInitID() {
        return initID;
    }

    public String getType() {
        return type;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public GatwaySubValueBoolean getIsCloudConnectionUp() {
        return IsCloudConnectionUp;
    }

    public ManagementPoint[] getManagementPoints() {
        return managementPoints;
    }

    public String getEmbeddedID() {
        return embeddedID;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
