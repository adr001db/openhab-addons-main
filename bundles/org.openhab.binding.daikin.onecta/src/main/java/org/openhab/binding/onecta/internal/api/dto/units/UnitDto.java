package org.openhab.binding.onecta.internal.api.dto.units;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitDto {
    private UUID id;
    private UUID initID;
    private String type;
    private String deviceModel;
    private IsCloudConnectionUp isCloudConnectionUp;
    private ManagementPoint[] managementPoints;
    private String embeddedID;
    private OffsetDateTime timestamp;
}
