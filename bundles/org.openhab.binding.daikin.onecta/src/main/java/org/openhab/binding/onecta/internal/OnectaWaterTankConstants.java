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
package org.openhab.binding.onecta.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link OnectaWaterTankConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Drent - Initial contribution
 */
@NonNullByDefault
public class OnectaWaterTankConstants {

    // List of all Channel ids

    public static final String CHANNEL_HWT_POWER = "hwtbasic#power";
    public static final String CHANNEL_HWT_ERRORCODE = "hwtbasic#errorcode";
    public static final String CHANNEL_HWT_IS_HOLIDAY_MODE_ACTIVE = "hwtbasic#isholidaymodeactive";
    public static final String CHANNEL_HWT_IS_IN_ERROR_STATE = "hwtbasic#isinerrorstate";
    public static final String CHANNEL_HWT_IS_IN_WARNING_STATE = "hwtbasic#isinwarningstate";
    public static final String CHANNEL_HWT_IS_IN_INSTALLER_STATE = "hwtbasic#isininstallerstate";
    public static final String CHANNEL_HWT_IS_IN_EMERGENCY_STATE = "hwtbasic#isinemergencystate";
    public static final String CHANNEL_HWT_IS_POWERFUL_MODE_ACTIVE = "hwtbasic#ispowerfulmodeactive";
    public static final String CHANNEL_HWT_POWERFUL_MODE = "hwtbasic#powerfulmode";
    public static final String CHANNEL_HWT_HEATUP_MODE = "hwtbasic#heatupmode";
    public static final String CHANNEL_HWT_TANK_TEMPERATURE = "hwtbasic#tanktemperature";
    public static final String CHANNEL_HWT_OPERATION_MODE = "hwtbasic#operationmode";
    public static final String CHANNEL_HWT_SETPOINT_MODE = "hwtbasic#setpointmode";
    public static final String CHANNEL_HWT_SETTEMP = "hwtbasic#settemp";
    public static final String CHANNEL_HWT_SETTEMP_MIN = "hwtbasic#settempmin";
    public static final String CHANNEL_HWT_SETTEMP_MAX = "hwtbasic#settempmax";
    public static final String CHANNEL_HWT_SETTEMP_STEP = "hwtbasic#settempstep";
    public static final String PROPERTY_HWT_NAME = "name";
}
