import de.rosenau.simon.network.api.IncomingRequest;
import de.rosenau.simon.network.api.NetworkHandler;
import de.rosenau.simon.network.api.OutgoingRequest;
import de.rosenau.simon.network.api.PacketDataSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TestRequest implements OutgoingRequest<TestResponse>, IncomingRequest<TestResponse> {

    private String name;

    @Override
    public void decode(PacketDataSerializer serializer) {
        this.name = serializer.readString();
    }

    @Override
    public TestResponse handle(NetworkHandler handler) {
        return new TestResponse(this.name + this.name);
    }

    @Override
    public void encode(PacketDataSerializer serializer) {
        serializer.writeString(this.name);
    }

}
