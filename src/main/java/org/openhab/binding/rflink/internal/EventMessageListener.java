/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rflink.internal;

import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.rflink.message.RfLinkMessage;

/**
 * The {@link EventMessageListener} is notified when a message is received.
 *
 *
 * @author Pauli Anttila - Initial contribution
 * @author cartemere - Refactor to build the RfLinkDevice instance within the Listener
 * @author cartemere - dissociate processing from eligibility
 */
public interface EventMessageListener {

    /**
     * This method is called by the bridge when a message is received
     *
     * @param message
     * @return true if the message can be processed, false otherwise
     */
    public boolean canHandleMessage(RfLinkMessage incomingMessage);

    /**
     * This method is called by the bridge when a message is received
     *
     * @param bridge
     * @param message
     * @throws Exception
     */
    public void handleIncomingMessage(ThingUID bridge, RfLinkMessage message) throws Exception;
}
