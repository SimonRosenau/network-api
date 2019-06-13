import de.rosenau.simon.network.api.IncomingPacket;
import de.rosenau.simon.network.api.NetworkClient;
import de.rosenau.simon.network.api.NetworkHandler;
import de.rosenau.simon.network.api.NetworkListener;
import de.rosenau.simon.network.impl.ClientBuilder;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 08.06.2019
 * Time: 21:57
 */

public class ClientTest {

    public static void main(String[] args) {

        NetworkClient client = new ClientBuilder().host("localhost").port(3000).build();
        //client.setAutoReconnect(true);
        client.setListener(new NetworkListener() {
            @Override
            public void onConnect(NetworkHandler handler) {
                System.out.println("Connected to server");

                handler.registerIncomingPacket(0, TestPacket.class);
                handler.registerOutgoingPacket(0, TestPacket.class);
            }

            @Override
            public void onReceive(NetworkHandler handler, IncomingPacket incomingPacket) {
                if (incomingPacket instanceof TestPacket) {
                    TestPacket packet = (TestPacket) incomingPacket;
                    System.out.println("Received: " + packet.getId() + " " + packet.getMessage() + " " + packet.getUuid() + " " + packet.getCount());
                    handler.reply(packet, new TestPacket("Response", "This is the response", UUID.randomUUID(), packet.getCount() + 1));
                }
            }

            @Override
            public void onDisconnect(NetworkHandler handler) {
                System.out.println("Disconnected from server");
                //client.disconnect();
            }

            @Override
            public void onError(NetworkHandler handler, Throwable cause) {
                //
                cause.printStackTrace();
            }
        });
        client.connect();
        try {
            Thread.sleep(10 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
