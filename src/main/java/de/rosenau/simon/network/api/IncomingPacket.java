package de.rosenau.simon.network.api;

/**
 * Project created by SIM0NSTR.
 */

public interface IncomingPacket {

    void decode(PacketDataSerializer serializer);

}
