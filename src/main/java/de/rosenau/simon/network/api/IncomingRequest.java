package de.rosenau.simon.network.api;

/**
 * Project created by SIM0NSTR.
 */

public interface IncomingRequest<T extends OutgoingResponse> {

    void decode(PacketDataSerializer serializer);
    T handle(NetworkHandler handler);

}
