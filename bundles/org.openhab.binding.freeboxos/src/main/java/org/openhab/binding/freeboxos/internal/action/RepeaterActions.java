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
package org.openhab.binding.freeboxos.internal.action;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.freeboxos.internal.handler.RepeaterHandler;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.binding.ThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {RepeaterActions} class is responsible to call corresponding actions on Freebox Repeater
 *
 * @author Gaël L'hopital - Initial contribution
 */
@ThingActionsScope(name = "freeboxos")
@NonNullByDefault
public class RepeaterActions implements ThingActions {
    private final Logger logger = LoggerFactory.getLogger(RepeaterActions.class);
    private @Nullable RepeaterHandler handler;

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof RepeaterHandler repeaterHandler) {
            this.handler = repeaterHandler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return handler;
    }

    @RuleAction(label = "reboot free repeater", description = "Reboots the Free Repeater")
    public void reboot() {
        logger.debug("Repeater reboot called");
        RepeaterHandler localHandler = this.handler;
        if (localHandler != null) {
            localHandler.reboot();
        } else {
            logger.warn("Repeater Action service ThingHandler is null");
        }
    }
}
