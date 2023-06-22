/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.onecta.internal.handler;

import static org.openhab.binding.onecta.internal.OnectaDeviceConstants.*;

import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.onecta.internal.OnectaConfiguration;
import org.openhab.binding.onecta.internal.api.Enums;
import org.openhab.binding.onecta.internal.service.DataTransportService;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OnectaDeviceHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Alexander Drent - Initial contribution
 */
@NonNullByDefault
public class OnectaDeviceHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(OnectaDeviceHandler.class);

    private @Nullable OnectaConfiguration config;

    private @Nullable ScheduledFuture<?> pollingJob;

    public OnectaDeviceHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_AC_POWER.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(OnectaConfiguration.class);
        DataTransportService dataTransService = new DataTransportService();
        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly, i.e. any network access must be done in
        // the background initialization below.
        // Also, before leaving this method a thing status from one of ONLINE, OFFLINE or UNKNOWN must be set. This
        // might already be the real thing status in case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        thing.setProperty(CHANNEL_AC_NAME, "");

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");
        //
        // Logging to INFO should be avoided normally.
        // See https://www.openhab.org/docs/developer/guidelines.html#f-logging

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    public void setUnit(DataTransportService dataTransService) {
        dataTransService.setData();
        if (dataTransService.isAvailable()) {

            getThing().setLabel(String.format("Daikin Onecta Unit (%s)", dataTransService.getUnitName()));
            getThing().setProperty(CHANNEL_AC_NAME, dataTransService.getUnitName());

            updateState(CHANNEL_AC_RAWDATA, new StringType(dataTransService.getRawData().toString()));

            updateState(CHANNEL_AC_POWER, OnOffType.from(dataTransService.getPowerOnOff()));
            updateState(CHANNEL_AC_OPERATIONMODE,
                    new StringType(dataTransService.getcurrentOperationMode().toString()));
            updateState(CHANNEL_AC_TEMP, (dataTransService.getCurrentTemperatureSet() == null ? UnDefType.UNDEF
                    : new DecimalType(dataTransService.getCurrentTemperatureSet())));
            updateState(CHANNEL_INDOOR_TEMP, (dataTransService.getIndoorTemperature() == null ? UnDefType.UNDEF
                    : new DecimalType(dataTransService.getIndoorTemperature())));
            updateState(CHANNEL_OUTDOOR_TEMP, (dataTransService.getOutdoorTemperature() == null ? UnDefType.UNDEF
                    : new DecimalType(dataTransService.getOutdoorTemperature())));
            updateState(CHANNEL_INDOOR_HUMIDITY, (dataTransService.getIndoorHumidity() == null ? UnDefType.UNDEF
                    : new DecimalType(dataTransService.getIndoorHumidity())));
            updateState(CHANNEL_AC_FANSPEED, new StringType(dataTransService.getCurrentFanspeed().toString()));
            updateState(CHANNEL_AC_FANMOVEMENT_HOR,
                    (dataTransService.getCurrentFanDirectionHor() == Enums.FanMovementHor.NOTAVAILABLE ? UnDefType.UNDEF
                            : new StringType(dataTransService.getCurrentFanDirectionHor().toString())));
            updateState(CHANNEL_AC_FANMOVEMENT_VER,
                    (dataTransService.getCurrentFanDirectionVer() == Enums.FanMovementVer.NOTAVAILABLE ? UnDefType.UNDEF
                            : new StringType(dataTransService.getCurrentFanDirectionVer().toString())));
            updateState(CHANNEL_AC_FANMOVEMENT, new StringType(dataTransService.getCurrentFanDirection().toString()));

        } else {
            getThing().setProperty(CHANNEL_AC_NAME, "Unit not registered at Onecta, unitID does not exists.");
        }
    }
}
