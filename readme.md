## Java NetworkApi

This is a Java Networking Api for a simple packed based Server and Client.

Install via maven:

```
<repository>
  <id>github</id>
  <name>GitHub Packages</name>
  <url>https://maven.pkg.github.com/SimonRosenau/network-api</url>
</repository>
```

```
<dependency>
  <groupId>de.rosenau.simon</groupId>
  <artifactId>networkapi</artifactId>
  <version>2.0.0</version>
</dependency>
```

Initialize Server:

```
NetworkServer server = new ServerBuilder().port(3000).build();
server.start();
```

Initialize Client:

```
NetworkClient client = new ClientBuilder().host("localhost").port(3000).build();
client.connect();
```

Set the listener to listen for connections and packets:

```
server.setListener(new NetworkListener() {
    @Override
    public void onConnect(NetworkHandler handler) {
        //
    }

    @Override
    public void onDisconnect(NetworkHandler handler) {
        //
    }

    @Override
    public void onError(NetworkHandler handler, Throwable cause) {
        //
    }
});
```

You can set the client to automatically retry to connect if it disconnects at any time with:

```
client.setKeepAlive(true);
```

You can create a packet like this:

```
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
        // Packet was decoded and is ready to handle here
    }

}
```

You need to register all packets with an id to map the packets to the right classes. You can do that simply by using:

```
handler.registerIncomingPacket(0, IncomingPacket.class);
handler.registerOutgoingPacket(0, OutgoingPacket.class);
```
 
When you send a packet with a given id, the packet is going to be serialized and send to the other party, which is then going to look for a registered IncomingPacket with the same id to properly deserialize the data.

Send a packet simply by using:
```
NetworkHandler handler = //
OutgoingPacket packet = new ... //
handler.sendPacket(packet);
```

If you're sending a packet in reply to another packet, use handler.reply():
```
NetworkHandler handler = //
IncomingPacket incomingPacket = //
OutgoingPacket packet = new ... //
handler.reply(incomingPacket, packet);
```

To listen for a response, pass a NetworkListener Instance as a second argument:

```
NetworkHandler handler = //
OutgoingPacket packet = new ... //
handler.sendPacket(packet, (responseHandler, responsePacket, throwable) -> {
    // Response received
};
```

Note that you can only reply to a packet, if the sender is listening for a response. The default onReceive method is not going to be called with a response packet.