package de.rosenau.simon.network.api;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 18:35
 */

public interface ResponseListener {

    void onResponse(NetworkHandler handler, IncomingPacket packet);

}
