import de.rosenau.simon.network.api.NetworkHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import de.rosenau.simon.network.api.IncomingPacket;
import de.rosenau.simon.network.api.OutgoingPacket;
import de.rosenau.simon.network.api.PacketDataSerializer;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 08.06.2019
 * Time: 22:10
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TestPacket implements IncomingPacket, OutgoingPacket {

    private String id;
    private String message;
    private UUID uuid;
    private int count;

    @Override
    public void encode(PacketDataSerializer serializer) {
        serializer.writeString(id);
        serializer.writeString(message);
        serializer.writeUUID(uuid);
        serializer.writeInt(count);
    }

    @Override
    public void decode(PacketDataSerializer serializer) {
        this.id = serializer.readString();
        this.message = serializer.readString();
        this.uuid = serializer.readUUID();
        this.count = serializer.readInt();
    }

    @Override
    public void handle(NetworkHandler handler) {

    }

}
