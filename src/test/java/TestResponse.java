import de.rosenau.simon.network.api.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;
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
public class TestResponse implements IncomingResponse, OutgoingResponse {

    private String string;

    @Override
    public void encode(PacketDataSerializer serializer) {
        serializer.writeString(string);
    }

    @Override
    public void decode(PacketDataSerializer serializer) {
        this.string = serializer.readString();
    }

}
