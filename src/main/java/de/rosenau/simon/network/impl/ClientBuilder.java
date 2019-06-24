package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.NetworkClient;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:38
 */

public class ClientBuilder {

    private String host;
    private int port;
    private String key;

    public ClientBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ClientBuilder key(String key) {
        this.key = key;
        return this;
    }

    public NetworkClient build() {
        return new NetworkClientImpl(host, port, key);
    }

}
