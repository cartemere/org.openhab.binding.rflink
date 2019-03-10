/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rflink.device;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.rflink.exceptions.RfLinkException;
import org.openhab.binding.rflink.exceptions.RfLinkNotImpException;
import org.openhab.binding.rflink.message.RfLinkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RFLink Message factory
 *
 * @author Cyril Cauchois - Initial contribution
 * @author Arjan Mels - Order of added keys is retained and search form first to last (to allow overlapping keywords to
 *         be handled properly)
 */
public class RfLinkDeviceFactory {

    private static Logger logger = LoggerFactory.getLogger(RfLinkDeviceFactory.class);

    private static LinkedHashMap<Predicate<RfLinkMessage>, Class<? extends RfLinkDevice>> MESSAGE_TO_DEVICE = new LinkedHashMap<>();
    private static HashMap<ThingTypeUID, Class<? extends RfLinkDevice>> THINGTYPE_TO_CLASS = new HashMap<>();

    /**
     * Mapping of the various message classes.
     * Note that the order is important: first matching class will be used
     */
    static {
        addMappingOfClass(RfLinkEnergyDevice.class);
        addMappingOfClass(RfLinkWindDevice.class);
        addMappingOfClass(RfLinkRainDevice.class);
        addMappingOfClass(RfLinkColorDevice.class);
        // addMappingOfClass(RfLinkTemperatureDevice.class);
        addMappingOfClass(RfLinkRtsDevice.class);
        // addMappingOfClass(RfLinkHumidityDevice.class);
        addMappingOfClass(RfLinkTempHygroDevice.class);
        addMappingOfClass(RfLinkSwitchDevice.class); // Switch class last as it is most generic
    }

    private static void addMappingOfClass(Class<? extends RfLinkDevice> _class) {
        try {
            RfLinkDevice m = _class.newInstance();
            MESSAGE_TO_DEVICE.put(m.eligibleMessageFunction(), _class);
            THINGTYPE_TO_CLASS.put(m.getThingType(), _class);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not map RfLinkDevice type : " + _class);
        }
    }

    public static RfLinkDevice createDeviceFromMessage(RfLinkMessage message)
            throws RfLinkException, RfLinkNotImpException {
        for (Map.Entry<Predicate<RfLinkMessage>, Class<? extends RfLinkDevice>> messageToDeviceEntry : MESSAGE_TO_DEVICE
                .entrySet()) {
            if (messageToDeviceEntry.getKey().test(message)) {
                Class<? extends RfLinkDevice> cl = messageToDeviceEntry.getValue();
                try {
                    Constructor<?> c = cl.getConstructor();
                    RfLinkDevice device = (RfLinkDevice) c.newInstance();
                    return device;
                } catch (Exception e) {
                    logger.error("Exception: {}", e);
                    throw new RfLinkException("unable to instanciate message object", e);
                }
            }
        }
        throw new RfLinkNotImpException("No message implementation found for packet " + message.rawMessage);
    }

    public static RfLinkDevice createDeviceFromType(ThingTypeUID thingType) throws RfLinkException {
        if (THINGTYPE_TO_CLASS.containsKey(thingType)) {
            try {
                Class<?> cl = THINGTYPE_TO_CLASS.get(thingType);
                Constructor<?> c = cl.getConstructor();
                return (RfLinkDevice) c.newInstance();
            } catch (Exception e) {
                throw new RfLinkException("Unable to instanciate message object", e);
            }
        }
        return null;
    }
}
