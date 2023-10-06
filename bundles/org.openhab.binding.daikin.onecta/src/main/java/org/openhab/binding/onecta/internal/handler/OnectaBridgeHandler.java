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

import static org.openhab.binding.onecta.internal.OnectaBridgeConstants.*;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.onecta.internal.OnectaConfiguration;
import org.openhab.binding.onecta.internal.api.OnectaConnectionClient;
import org.openhab.binding.onecta.internal.api.dto.units.Units;
import org.openhab.binding.onecta.internal.exception.DaikinCommunicationException;
import org.openhab.binding.onecta.internal.service.DeviceDiscoveryService;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OnectaBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Alexander Drent - Initial contribution
 */
@NonNullByDefault
public class OnectaBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(OnectaBridgeHandler.class);

    private @Nullable OnectaConfiguration config;

    private @Nullable ScheduledFuture<?> pollingJob;

    private Units units = new Units();

    public Units getUnits() {
        return units;
    }

    private @Nullable DeviceDiscoveryService deviceDiscoveryService;

    /**
     * Defines a runnable for a discovery
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (deviceDiscoveryService != null) {
                deviceDiscoveryService.discoverDevices();
            }
        }
    };

    public OnectaBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // if (CHANNEL_1.equals(channelUID.getId())) {
        // if (command instanceof RefreshType) {
        // // TODO: handle data refresh
        // }
        //
        // // TODO: handle command
        //
        // // Note: if communication with thing fails for some reason,
        // // indicate that by setting the status with detail information:
        // // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // // "Could not control device at IP address x.x.x.x");
        // }
    }

    @Override
    public void initialize() {
        config = getConfigAs(OnectaConfiguration.class);

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
        try {
            String refreshToken = thing.getConfiguration().get(CHANNEL_REFRESH_TOKEN) == null ? ""
                    : thing.getConfiguration().get(CHANNEL_REFRESH_TOKEN).toString();
            OnectaConnectionClient.startConnecton(thing.getConfiguration().get(CHANNEL_USERID).toString(),
                    thing.getConfiguration().get(CHANNEL_PASSWORD).toString(), refreshToken);

            if (OnectaConnectionClient.isOnline()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        } catch (DaikinCommunicationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }

        // Example for background initialization:
        // scheduler.execute(() -> {

        // try {
        // //units = onactaConnectionClient.getUnits();
        // updateStatus(ThingStatus.ONLINE);
        // } catch (DaikinCommunicationException e) {
        // updateStatus(ThingStatus.OFFLINE);
        // }
        //
        // });
        pollingJob = scheduler.scheduleWithFixedDelay(this::pollDevices, 10,
                Integer.parseInt(thing.getConfiguration().get(CHANNEL_REFRESHINTERVAL).toString()), TimeUnit.SECONDS);

        // Trigger discovery of Devices
        scheduler.submit(runnable);

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

    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
        ScheduledFuture<?> pollingJob = this.pollingJob;
        if (pollingJob != null) {
            pollingJob.cancel(true);
            this.pollingJob = null;
        }
    }

    private void pollDevices() {
        logger.debug("pollDevices.");
        if (OnectaConnectionClient.isOnline()) {
            updateStatus(ThingStatus.ONLINE);

            getThing().getConfiguration().put(CHANNEL_REFRESH_TOKEN, OnectaConnectionClient.getRefreshToken());

        } else {
            if (getThing().getStatus() != ThingStatus.OFFLINE) {
                updateStatus(ThingStatus.OFFLINE);
            }
        }
        try {
            OnectaConnectionClient.refreshUnitsData(getThing());
            // if (thing.getConfiguration().get("showAvailableUnitsInLog").toString() == "true") {
            //
            // for (Unit unit : units.getAll()) {
            // logger.info("Available Daikin unit UID : '{}' - '{}' .", unit.getId(), unit
            // .findManagementPointsById(ManagementPoint.CLIMATECONTROL.getValue()).getName().getValue());
            // }
            // }
        } catch (DaikinCommunicationException e) {
            logger.debug("DaikinCommunicationException: " + e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }

        List<Thing> things = getThing().getThings();

        for (Thing t : things) {
            // BaseThingHandler handler;
            if (t.getThingTypeUID().equals(DEVICE_THING_TYPE)) {
                OnectaDeviceHandler onectaDeviceHandler = (OnectaDeviceHandler) t.getHandler();
                onectaDeviceHandler.refreshDevice();
            } else if (t.getThingTypeUID().equals(GATEWAY_THING_TYPE)) {
                OnectaGatewayHandler onectaGatewayHandler = (OnectaGatewayHandler) t.getHandler();
                onectaGatewayHandler.refreshDevice();
            } else
                continue;

            // if (handler == null) {
            // logger.trace("no handler for thing: {}", t.getUID());
            // continue;
            // }

        }
    }

    public void setDiscovery(DeviceDiscoveryService deviceDiscoveryService) {
        this.deviceDiscoveryService = deviceDiscoveryService;
    }
}
