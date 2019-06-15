package de.rosenau.simon.network.api;

import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:35
 */

public interface NetworkClient {

    void connect();
    void disconnect();
    void setAutoReconnect(boolean keepAlive);
    boolean isAutoReconnect();
    void setListener(NetworkListener listener);
    InetSocketAddress getAddress();
    boolean isConnected();

}
