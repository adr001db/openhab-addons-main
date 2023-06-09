package org.openhab.binding.onecta.internal.api.dto.units;

import java.util.List;
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
    private List<ManagementPoint> managementPoints;
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

    public List<ManagementPoint> getManagementPoints() {
        return managementPoints;
    }

    public String getEmbeddedID() {
        return embeddedID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ManagementPoint findManagementPointsById(String key) {
        return managementPoints.stream()
                .filter(managementPoint -> key.equals(managementPoint.getEmbeddedId().toString())).findFirst()
                .orElse(null);
    }
}
