import de.rosenau.simon.network.api.IncomingPacket;
import de.rosenau.simon.network.api.NetworkHandler;
import de.rosenau.simon.network.api.OutgoingPacket;
import de.rosenau.simon.network.api.PacketDataSerializer;

public class TestPacket implements OutgoingPacket, IncomingPacket {

    @Override
    public void encode(PacketDataSerializer serializer) {
        serializer.writeString("Test123");
    }

    private String string;

    @Override
    public void decode(PacketDataSerializer serializer) {
        this.string = serializer.readString();
    }

    @Override
    public void handle(NetworkHandler handler) {
        System.out.println(string);
    }

}
