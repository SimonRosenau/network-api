package de.rosenau.simon.network.api;

/**
 * Project created by SIM0NSTR.
 */

public interface OutgoingPacket {

    void encode(PacketDataSerializer serializer);

}
