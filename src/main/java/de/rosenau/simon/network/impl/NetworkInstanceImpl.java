package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.NetworkHandler;
import lombok.Setter;
import de.rosenau.simon.network.api.NetworkListener;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:17
 */

abstract class NetworkInstanceImpl {

    @Setter
    NetworkListener listener;

    void onConnect(NetworkHandler handler) {
        if (listener != null) listener.onConnect(handler);
    }

    void onDisconnect(NetworkHandler handler) {
        if (listener != null) listener.onDisconnect(handler);
    }

    abstract String getKey();

    abstract boolean isDebug();

}
