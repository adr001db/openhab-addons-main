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
package org.openhab.binding.hue.internal.dto.clip2;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.hue.internal.dto.clip2.enums.ButtonEventType;

import com.google.gson.annotations.SerializedName;

/**
 * DTO for CLIP 2 button state.
 *
 * @author Andrew Fiddian-Green - Initial contribution
 */
@NonNullByDefault
public class Button {
    private @Nullable @SerializedName("last_event") String lastEvent;

    /**
     * @return the last button event as an enum.
     * @throws IllegalArgumentException if lastEvent is null or bad.
     */
    public ButtonEventType getLastEvent() throws IllegalArgumentException {
        String lastEvent = this.lastEvent;
        if (Objects.nonNull(lastEvent)) {
            return ButtonEventType.valueOf(lastEvent.toUpperCase());
        }
        throw new IllegalArgumentException("lastEvent field is null");
    }
}
