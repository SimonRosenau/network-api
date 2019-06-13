package de.rosenau.simon.network.api;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:35
 */

public interface NetworkClient {

    void connect();
    void disconnect();
    void setKeepAlive(boolean keepAlive);
    boolean isKeepAlive();
    void setListener(NetworkListener listener);

}
