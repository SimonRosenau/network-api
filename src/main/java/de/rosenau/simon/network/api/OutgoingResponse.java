package de.rosenau.simon.network.api;

/**
 * Project created by SIM0NSTR.
 */

public interface OutgoingResponse {

    void encode(PacketDataSerializer serializer);

}
