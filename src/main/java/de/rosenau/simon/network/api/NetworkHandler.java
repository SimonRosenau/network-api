package de.rosenau.simon.network.api;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Project created by SIM0NSTR.
 */

public interface NetworkHandler {

    void sendPacket(OutgoingPacket packet);

    void sendPacket(OutgoingPacket packet, ResponseListener listener);

    void reply(IncomingPacket incomingPacket, OutgoingPacket packet);

    void disconnect();

    InetSocketAddress remoteAddress();

    void registerIncomingPacket(int id, Class<? extends IncomingPacket> c);

    void registerOutgoingPacket(int id, Class<? extends OutgoingPacket> c);

}
