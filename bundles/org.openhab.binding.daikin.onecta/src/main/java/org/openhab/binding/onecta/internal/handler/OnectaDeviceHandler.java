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

import java.time.LocalDate;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.onecta.internal.OnectaConfiguration;
import org.openhab.binding.onecta.internal.api.Enums;
import org.openhab.binding.onecta.internal.service.ChannelsRefreshDelay;
import org.openhab.binding.onecta.internal.service.DataTransportService;
import org.openhab.core.library.types.*;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
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

    private final DataTransportService dataTransService;
    private @Nullable ChannelsRefreshDelay channelsRefreshDelay;

    public OnectaDeviceHandler(Thing thing) {
        super(thing);
        this.dataTransService = new DataTransportService(thing.getConfiguration().get("unitID").toString(),
                Enums.ManagementPoint.CLIMATECONTROL);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        try {
            channelsRefreshDelay.add(channelUID.getId());
            switch (channelUID.getId()) {
                case CHANNEL_AC_POWER:
                    if (command instanceof OnOffType) {
                        dataTransService.setPowerOnOff(Enums.OnOff.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_POWERFULMODE:
                    if (command instanceof OnOffType) {
                        dataTransService.setPowerFulModeOnOff(Enums.OnOff.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_OPERATIONMODE:
                    if (command instanceof StringType) {
                        dataTransService.setCurrentOperationMode(Enums.OperationMode.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_TEMP:
                    if (command instanceof QuantityType) {
                        dataTransService.setCurrentTemperatureSet(((QuantityType<?>) command).floatValue());
                    }
                    break;
                case CHANNEL_AC_FANSPEED:
                    if (command instanceof StringType) {
                        dataTransService.setFanSpeed(Enums.FanSpeed.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_FANMOVEMENT:
                    if (command instanceof StringType) {
                        dataTransService.setCurrentFanDirection(Enums.FanMovement.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_FANMOVEMENT_HOR:
                    if (command instanceof StringType) {
                        dataTransService.setCurrentFanDirectionHor(Enums.FanMovementHor.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_FANMOVEMENT_VER:
                    if (command instanceof StringType) {
                        dataTransService.setCurrentFanDirectionVer(Enums.FanMovementVer.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_ECONOMODE:
                    if (command instanceof OnOffType) {
                        dataTransService.setEconoMode(Enums.OnOff.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_STREAMER:
                    if (command instanceof OnOffType) {
                        dataTransService.setStreamerMode(Enums.OnOff.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_HOLIDAYMODE:
                    if (command instanceof OnOffType) {
                        dataTransService.setHolidayMode(Enums.OnOff.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_DEMANDCONTROL:
                    if (command instanceof StringType) {
                        dataTransService.setDemandControl(Enums.DemandControl.valueOf(command.toString()));
                    }
                    break;
                case CHANNEL_AC_DEMANDCONTROLFIXEDVALUE:
                    if (command instanceof QuantityType) {
                        dataTransService.setDemandControlFixedValue(((QuantityType<?>) command).intValue());
                    }
                    break;
                case CHANNEL_AC_TARGETTEMP:
                    if (command instanceof QuantityType) {
                        dataTransService.setTargetTemperatur(((QuantityType<?>) command).intValue());
                    }
                    break;
            }

            updateStatus(ThingStatus.ONLINE);
        } catch (Exception ex) {
            // catch exceptions and handle it in your binding
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, ex.getMessage());
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(OnectaConfiguration.class);
        channelsRefreshDelay = new ChannelsRefreshDelay(
                Long.parseLong(thing.getConfiguration().get("refreshDelay").toString()) * 1000);
        // DataTransportService dataTransService = new DataTransportService();
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

    public void refreshDevice() {
        logger.debug("refreshDevice :" + dataTransService.getUnitName());
        dataTransService.refreshUnit();

        if (dataTransService.isAvailable()) {

            // getThing().setLabel(String.format("Daikin Onecta Unit (%s)", dataTransService.getUnitName()));
            getThing().setProperty(PROPERTY_AC_NAME, dataTransService.getUnitName());

            updateState(CHANNEL_AC_RAWDATA, new StringType(dataTransService.getRawData().toString()));

            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_POWER))
                updateState(CHANNEL_AC_POWER, OnOffType.from(dataTransService.getPowerOnOff()));
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_POWERFULMODE))
                updateState(CHANNEL_AC_POWERFULMODE, OnOffType.from(dataTransService.getPowerFulModeOnOff()));

            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_OPERATIONMODE))
                updateState(CHANNEL_AC_OPERATIONMODE,
                        new StringType(dataTransService.getCurrentOperationMode().toString()));

            // Set Temp
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TEMP))
                updateState(CHANNEL_AC_TEMP, getCurrentTemperatureSet());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TEMPMIN))
                updateState(CHANNEL_AC_TEMPMIN, getCurrentTemperatureSetMin());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TEMPMAX))
                updateState(CHANNEL_AC_TEMPMAX, getCurrentTemperatureSetMax());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TEMPSTEP))
                updateState(CHANNEL_AC_TEMPSTEP, getCurrentTemperatureSetStep());

            // Target temp
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TARGETTEMP))
                updateState(CHANNEL_AC_TARGETTEMP, getTargetTemperatur());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TARGETTEMPMIN))
                updateState(CHANNEL_AC_TARGETTEMPMIN, getTargetTemperaturMin());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TARGETTEMPMAX))
                updateState(CHANNEL_AC_TARGETTEMPMAX, getTargetTemperaturMax());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_TARGETTEMPSTEP))
                updateState(CHANNEL_AC_TARGETTEMPSTEP, getTargetTemperaturStep());

            // Fan
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_FANMOVEMENT))
                updateState(CHANNEL_AC_FANMOVEMENT, getCurrentFanDirection());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_FANSPEED))
                updateState(CHANNEL_AC_FANSPEED, getCurrentFanspeed());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_ECONOMODE))
                updateState(CHANNEL_AC_ECONOMODE, getEconoMode());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_STREAMER))
                updateState(CHANNEL_AC_STREAMER, getStreamerMode());

            updateState(CHANNEL_INDOOR_TEMP, getIndoorTemperature());
            updateState(CHANNEL_OUTDOOR_TEMP, getOutdoorTemperature());
            updateState(CHANNEL_LEAVINGWATER_TEMP, getLeavingWaterTemperatur());
            updateState(CHANNEL_INDOOR_HUMIDITY, getIndoorHumidity());
            updateState(CHANNEL_AC_TIMESTAMP, getTimestamp());

            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_FANMOVEMENT_HOR))
                updateState(CHANNEL_AC_FANMOVEMENT_HOR, getCurrentFanDirectionHor());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_FANMOVEMENT_VER))
                updateState(CHANNEL_AC_FANMOVEMENT_VER, getCurrentFanDirectionVer());

            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_HOLIDAYMODE))
                updateState(CHANNEL_AC_HOLIDAYMODE, getHolidayMode());
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_DEMANDCONTROL))
                updateState(CHANNEL_AC_DEMANDCONTROL, getDemandControl());

            // DEMANDCONTROL
            if (channelsRefreshDelay.isDelayPassed(CHANNEL_AC_DEMANDCONTROLFIXEDVALUE))
                updateState(CHANNEL_AC_DEMANDCONTROLFIXEDVALUE, getDemandControlFixedValue());
            updateState(CHANNEL_AC_DEMANDCONTROLFIXEDSTEPVALUE, getDemandControlFixedStepValue());
            updateState(CHANNEL_AC_DEMANDCONTROLFIXEDMINVALUE, getDemandControlFixedMinValue());
            updateState(CHANNEL_AC_DEMANDCONTROLFIXEDMAXVALUE, getDemandControlFixedMaxValue());

            // Energy consumption Cooling Day
            if (dataTransService.getConsumptionCoolingDay() != null) {
                for (int i = 0; i < dataTransService.getConsumptionCoolingDay().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_COOLING_DAY, i),
                            dataTransService.getConsumptionCoolingDay()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionCoolingDay()[i]));
                }
            }
            // Energy consumption Cooling Week
            if (dataTransService.getConsumptionCoolingWeek() != null) {
                for (int i = 0; i < dataTransService.getConsumptionCoolingWeek().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_COOLING_WEEK, i),
                            dataTransService.getConsumptionCoolingWeek()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionCoolingWeek()[i]));
                }
            }
            // Energy consumption Cooling Month
            if (dataTransService.getConsumptionCoolingMonth() != null) {
                for (int i = 0; i < dataTransService.getConsumptionCoolingMonth().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_COOLING_MONTH, i),
                            dataTransService.getConsumptionCoolingMonth()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionCoolingMonth()[i]));
                }
            }

            // Energy consumption Heating Day
            if (dataTransService.getConsumptionHeatingDay() != null) {
                for (int i = 0; i < dataTransService.getConsumptionHeatingDay().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_HEATING_DAY, i),
                            dataTransService.getConsumptionHeatingDay()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionHeatingDay()[i]));
                }
            }
            // Energy consumption Heating Week
            if (dataTransService.getConsumptionHeatingWeek() != null) {
                for (int i = 0; i < dataTransService.getConsumptionHeatingWeek().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_HEATING_WEEK, i),
                            dataTransService.getConsumptionHeatingWeek()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionHeatingWeek()[i]));
                }
            }
            // Energy consumption Heating Month
            if (dataTransService.getConsumptionHeatingMonth() != null) {
                for (int i = 0; i < dataTransService.getConsumptionHeatingMonth().length; i++) {
                    updateState(String.format(CHANNEL_AC_ENERGY_HEATING_MONTH, i),
                            dataTransService.getConsumptionHeatingMonth()[i] == null ? UnDefType.UNDEF
                                    : new DecimalType(dataTransService.getConsumptionHeatingMonth()[i]));
                }
            }
            // calculate current day and year energy consumption
            updateState(CHANNEL_AC_ENERGY_HEATING_CURRENT_DAY, getEnergyHeatingCurrentDay());
            updateState(CHANNEL_AC_ENERGY_HEATING_CURRENT_YEAR, getEnergyHeatingCurrentYear());
            updateState(CHANNEL_AC_ENERGY_COOLING_CURRENT_DAY, getEnergyCoolingCurrentDay());
            updateState(CHANNEL_AC_ENERGY_COOLING_CURRENT_YEAR, getEnergyCoolingCurrentYear());

        } else {
            getThing().setProperty(PROPERTY_AC_NAME, "Unit not registered at Onecta, unitID does not exists.");
        }
    }

    private State getCurrentFanspeed() {
        try {
            return new StringType(dataTransService.getCurrentFanspeed().toString());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentTemperatureSet() {
        try {
            return new DecimalType(dataTransService.getCurrentTemperatureSet());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentTemperatureSetMin() {
        try {
            return new DecimalType(dataTransService.getCurrentTemperatureSetMin());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentTemperatureSetMax() {
        try {
            return new DecimalType(dataTransService.getCurrentTemperatureSetMax());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentTemperatureSetStep() {
        try {
            return new DecimalType(dataTransService.getCurrentTemperatureSetStep());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getOutdoorTemperature() {
        try {
            return new DecimalType(dataTransService.getOutdoorTemperature());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getIndoorTemperature() {
        try {
            return new DecimalType(dataTransService.getIndoorTemperature());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getLeavingWaterTemperatur() {
        try {
            return new DecimalType(dataTransService.getLeavingWaterTemperature());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getTargetTemperatur() {
        try {
            return new DecimalType(dataTransService.getTargetTemperatur());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getTargetTemperaturMax() {
        try {
            return new DecimalType(dataTransService.getTargetTemperaturMax());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getTargetTemperaturMin() {
        try {
            return new DecimalType(dataTransService.getTargetTemperaturMin());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getTargetTemperaturStep() {
        try {
            return new DecimalType(dataTransService.getTargetTemperaturStep());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getDemandControlFixedValue() {
        try {
            return new DecimalType(dataTransService.getDemandControlFixedValue());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getDemandControlFixedStepValue() {
        try {
            return new DecimalType(dataTransService.getDemandControlFixedStepValue());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getDemandControlFixedMinValue() {
        try {
            return new DecimalType(dataTransService.getDemandControlFixedMinValue());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getDemandControlFixedMaxValue() {
        try {
            return new DecimalType(dataTransService.getDemandControlFixedMaxValue());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getIndoorHumidity() {
        try {
            return new DecimalType(dataTransService.getIndoorHumidity());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getTimestamp() {
        try {
            return new DateTimeType(dataTransService.getTimeStamp());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getEconoMode() {
        try {
            return OnOffType.from(dataTransService.getEconoMode());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getStreamerMode() {
        try {
            return OnOffType.from(dataTransService.getStreamerMode());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentFanDirectionHor() {
        try {
            return new StringType(dataTransService.getCurrentFanDirectionHor().toString());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentFanDirectionVer() {
        try {
            return new StringType(dataTransService.getCurrentFanDirectionVer().toString());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getCurrentFanDirection() {
        try {
            return new StringType(dataTransService.getCurrentFanDirection().toString());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getHolidayMode() {
        try {
            return OnOffType.from(dataTransService.getHolidayMode());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getDemandControl() {
        try {
            return new StringType(dataTransService.getDemandControl().toString());
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private int getCurrentDayOfWeek() {
        LocalDate today = LocalDate.now();
        return today.getDayOfWeek().getValue() - 1;
    }

    private State getEnergyHeatingCurrentDay() {
        try {
            return new DecimalType(dataTransService.getConsumptionHeatingWeek()[7 + getCurrentDayOfWeek()]);
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getEnergyCoolingCurrentDay() {
        try {
            return new DecimalType(dataTransService.getConsumptionCoolingWeek()[7 + getCurrentDayOfWeek()]);
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getEnergyCoolingCurrentYear() {
        double total = 0;
        try {
            for (int i = 12; i < 23; i++) {
                if (dataTransService.getConsumptionCoolingMonth()[i] != null) {
                    total += dataTransService.getConsumptionCoolingMonth()[i];
                }
            }
            return new DecimalType(Math.round(total * 10) / 10D);
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }

    private State getEnergyHeatingCurrentYear() {
        double total = 0;
        try {
            for (int i = 12; i < 23; i++) {
                if (dataTransService.getConsumptionHeatingMonth()[i] != null) {
                    total += dataTransService.getConsumptionHeatingMonth()[i];
                }
            }
            return new DecimalType(Math.round(total * 10) / 10D);
        } catch (Exception e) {
            return UnDefType.UNDEF;
        }
    }
}
