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
package org.openhab.binding.solax.internal.model.parsers;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.solax.internal.connectivity.rawdata.LocalConnectRawDataBean;
import org.openhab.binding.solax.internal.model.InverterData;

/**
 * The {@link RawDataParser} declares generic parser implementation that parses raw data to generic inverter data which
 * is common for all inverters.
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public interface RawDataParser {

    InverterData getData(LocalConnectRawDataBean bean);

    Set<String> getSupportedChannels();
}
