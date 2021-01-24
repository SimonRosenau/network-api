package de.rosenau.simon.network.api;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Project created by SIM0NSTR.
 */

public interface NetworkHandler {

    void send(OutgoingPacket packet);

    <T extends IncomingResponse> T request(OutgoingRequest<T> request) throws RuntimeException;

    void disconnect();

    InetSocketAddress remoteAddress();

    void registerIncomingPacket(int id, Class<? extends IncomingPacket> c);

    void registerOutgoingPacket(int id, Class<? extends OutgoingPacket> c);

    void registerIncomingRequest(int id, Class<? extends IncomingRequest<?>> c);

    <T extends IncomingResponse> void registerOutgoingRequest(int id, Class<? extends OutgoingRequest<T>> c, Class<T> rc);

}
