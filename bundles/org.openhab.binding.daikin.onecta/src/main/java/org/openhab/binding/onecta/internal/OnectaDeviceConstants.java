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
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link OnectaDeviceConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Drent - Initial contribution
 */
@NonNullByDefault
public class OnectaDeviceConstants {

    private static final String BINDING_ID = "onecta";
    public static final String BRIDGE = "account";
    // List of all Device Types
    public static final String DEVICE = "device";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");

    // List of Bridge Type

    // List of all Bridge Thing Type UIDs
    public static final ThingTypeUID BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, BRIDGE);

    // List of all Thing Type UIDs
    public static final ThingTypeUID DEVICE_THING_TYPE = new ThingTypeUID(BINDING_ID, DEVICE);

    // List of all Channel ids
    public static final String CHANNEL_AC_TEMP = "basic#settemp";
    public static final String CHANNEL_INDOOR_TEMP = "basic#indoortemp";
    public static final String CHANNEL_INDOOR_HUMIDITY = "basic#humidity";

    public static final String CHANNEL_OUTDOOR_TEMP = "basic#outdoortemp";
    public static final String CHANNEL_AC_POWER = "basic#power";
    public static final String CHANNEL_AC_RAWDATA = "extra#rawdata";
    public static final String CHANNEL_AC_OPERATIONMODE = "basic#operationmode";
    public static final String CHANNEL_AC_NAME = "basic#name";
    public static final String CHANNEL_AC_FANSPEED = "basic#fanspeed";
    public static final String CHANNEL_AC_FANMOVEMENT_HOR = "fbasic#andirhor";
    public static final String CHANNEL_AC_FANMOVEMENT_VER = "basic#fandirver";
    public static final String CHANNEL_AC_FANMOVEMENT = "basic#fandir";
    public static final String CHANNEL_AC_ECONOMODE = "basic#economode";
    public static final String CHANNEL_AC_STREAMER = "basic#streamer";
    public static final String CHANNEL_AC_HOLIDAYMODE = "basic#holidaymode";
    public static final String CHANNEL_AC_DEMANDCONTROL = "demandcontrol#demandcontrol";
    public static final String CHANNEL_AC_DEMANDCONTROLFIXEDVALUE = "demandcontrol#demandcontrolfixedvalue";
    public static final String CHANNEL_AC_DEMANDCONTROLFIXEDSTEPVALUE = "demandcontrol#demandcontrolfixedstepvalue";
    public static final String CHANNEL_AC_DEMANDCONTROLFIXEDMINVALUE = "demandcontrol#demandcontrolfixedminvalue";
    public static final String CHANNEL_AC_DEMANDCONTROLFIXEDMAXVALUE = "demandcontrol#demandcontrolfixedmaxvalue";
}
