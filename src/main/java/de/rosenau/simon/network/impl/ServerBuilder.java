package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.NetworkServer;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:30
 */

public class ServerBuilder {

    private int port;
    private String key;

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder key(String key) {
        this.key = key;
        return this;
    }

    public NetworkServer build() {
        return new NetworkServerImpl(port, key);
    }

}
