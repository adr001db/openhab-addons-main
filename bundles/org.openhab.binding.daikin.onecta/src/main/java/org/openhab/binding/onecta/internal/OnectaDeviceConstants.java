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
    public static final String CHANNEL_AC_TEMP = "settemp";
    public static final String CHANNEL_INDOOR_TEMP = "indoortemp";
    public static final String CHANNEL_INDOOR_HUMIDITY = "humidity";

    public static final String CHANNEL_OUTDOOR_TEMP = "outdoortemp";
    public static final String CHANNEL_AC_POWER = "power";
    public static final String CHANNEL_AC_RAWDATA = "rawdata";
    public static final String CHANNEL_AC_OPERATIONMODE = "operationmode";
    public static final String CHANNEL_AC_NAME = "name";
    public static final String CHANNEL_AC_FANSPEED = "fanspeed";
    public static final String CHANNEL_AC_FANMOVEMENT_HOR = "fandirhor";
    public static final String CHANNEL_AC_FANMOVEMENT_VER = "fandirver";
    public static final String CHANNEL_AC_FANMOVEMENT = "fandir";
    public static final String CHANNEL_AC_ECONOMODE = "economode";
}
