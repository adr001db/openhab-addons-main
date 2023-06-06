package org.openhab.binding.onecta.internal.api.dto.units;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurpleOperationModes {
    private FanOnlyClass heating;
    private FanOnlyClass cooling;
    private FanOnlyClass auto;
    private OperationModesDry dry;
    private FanOnlyClass fanOnly;
}
