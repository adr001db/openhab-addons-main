package org.openhab.binding.onecta.internal.api.dto.units;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionTypesFanSpeed {
    private ActionTypes heating;
    private ActionTypes cooling;
    private ActionTypes auto;
    private FanSpeedDry dry;
    private ActionTypes fanOnly;
}
