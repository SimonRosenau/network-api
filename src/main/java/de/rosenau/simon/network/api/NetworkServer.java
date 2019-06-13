package de.rosenau.simon.network.api;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:29
 */

public interface NetworkServer {

    void start();
    void stop();
    int getPort();
    void setListener(NetworkListener listener);

}
