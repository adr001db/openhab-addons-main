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
package org.openhab.binding.shelly.internal.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.shelly.internal.discovery.ShellyBluDiscoveryService;
import org.openhab.core.thing.ThingTypeUID;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

/***
 * The{@link ShellyThingTable} implements a simple table to allow dispatching incoming events to the proper thing
 * handler
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = ShellyThingTable.class, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ShellyThingTable {
    private Map<String, ShellyThingInterface> thingTable = new ConcurrentHashMap<>();
    private @Nullable ShellyBluDiscoveryService bluDiscoveryService;

    public void addThing(String key, ShellyThingInterface thing) {
        if (thingTable.containsKey(key)) {
            thingTable.remove(key);
        }
        thingTable.put(key, thing);
    }

    public @Nullable ShellyThingInterface findThing(String key) {
        ShellyThingInterface t = thingTable.get(key);
        if (t != null) {
            return t;
        }
        for (Map.Entry<String, ShellyThingInterface> entry : thingTable.entrySet()) {
            t = entry.getValue();
            if (t.checkRepresentation(key)) {
                return t;
            }
        }
        return null;
    }

    public ShellyThingInterface getThing(String key) {
        ShellyThingInterface t = findThing(key);
        if (t == null) {
            throw new IllegalArgumentException();
        }
        return t;
    }

    public void removeThing(String key) {
        if (thingTable.containsKey(key)) {
            thingTable.remove(key);
        }
    }

    public Map<String, ShellyThingInterface> getTable() {
        return thingTable;
    }

    public int size() {
        return thingTable.size();
    }

    public void startDiscoveryService(BundleContext bundleContext) {
        if (bluDiscoveryService == null) {
            bluDiscoveryService = new ShellyBluDiscoveryService(bundleContext, this);
            bluDiscoveryService.registerDeviceDiscoveryService();
        }
    }

    public void startScan() {
        for (Map.Entry<String, ShellyThingInterface> thing : thingTable.entrySet()) {
            (thing.getValue()).startScan();
        }
    }

    public void stopDiscoveryService() {
        if (bluDiscoveryService != null) {
            bluDiscoveryService.unregisterDeviceDiscoveryService();
            bluDiscoveryService = null;
        }
    }

    public void discoveredResult(ThingTypeUID uid, String model, String serviceName, String address,
            Map<String, Object> properties) {
        if (bluDiscoveryService != null) {
            bluDiscoveryService.discoveredResult(uid, model, serviceName, address, properties);
        }
    }

    @Deactivate
    public void deactivate() {
        stopDiscoveryService();
    }
}
