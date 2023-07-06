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
package org.openhab.binding.onecta.internal.api;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link OnectaProperties} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Drent - Initial contribution
 */
@NonNullByDefault
public class OnectaProperties {

    private static final String BASE_URL_COMMAND = "/%s/management-points/climateControl/characteristics/%s";
    private static final String BASE_COMMAND_ONOFF = "onOffMode";

    public static String getBaseUrl(String unitId) {
        return String.format(BASE_URL_COMMAND, unitId, "");
    }

    public static String getUrlOnOff(String unitId) {
        return String.format(BASE_URL_COMMAND, unitId, BASE_COMMAND_ONOFF);
    }
}
