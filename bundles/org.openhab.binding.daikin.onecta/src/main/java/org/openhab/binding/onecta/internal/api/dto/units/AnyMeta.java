package org.openhab.binding.onecta.internal.api.dto.units;

import java.time.OffsetTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnyMeta {
    private OffsetTime minIntervalBetweenActions;
    private long maxSchedules;
    private long maxActionsPerActionPeriod;
    private boolean consecutiveActionsAllowed;
    private PurpleActionTypes actionTypes;
}
