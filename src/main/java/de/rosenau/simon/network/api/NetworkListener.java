package de.rosenau.simon.network.api;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:09
 */

public interface NetworkListener {
    void onConnect(NetworkHandler handler);
    void onDisconnect(NetworkHandler handler);
    void onError(NetworkHandler handler, Throwable cause);
}