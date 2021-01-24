package de.rosenau.simon.network.api;

public interface OutgoingRequest<T extends IncomingResponse> {

    void encode(PacketDataSerializer serializer);

}
