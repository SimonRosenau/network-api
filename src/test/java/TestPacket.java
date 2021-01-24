import de.rosenau.simon.network.api.OutgoingPacket;
import de.rosenau.simon.network.api.PacketDataSerializer;

public class TestPacket implements OutgoingPacket {

    @Override
    public void encode(PacketDataSerializer serializer) {
        serializer.writeString("Test123");
    }

}
