import de.rosenau.simon.network.api.IncomingPacket;
import de.rosenau.simon.network.api.NetworkHandler;
import de.rosenau.simon.network.api.NetworkListener;
import de.rosenau.simon.network.api.NetworkServer;
import de.rosenau.simon.network.impl.ServerBuilder;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 08.06.2019
 * Time: 21:58
 */

public class ServerTest {

    public static void main(String[] args) {

        NetworkServer server = new ServerBuilder().port(3000).key("ffffffff").build();
        server.setListener(new NetworkListener() {
            @Override
            public void onConnect(NetworkHandler handler) {
                System.out.println("Client connected: " + handler.remoteAddress().toString());

                handler.registerIncomingPacket(0, TestPacket.class);
                handler.registerOutgoingPacket(0, TestPacket.class);

                handler.sendPacket(new TestPacket("Message", "This is the Message", UUID.randomUUID(), 0), (handler1, incomingPacket) -> {
                    if (incomingPacket instanceof TestPacket) {
                        TestPacket packet = (TestPacket) incomingPacket;
                        System.out.println("Received Test Packet Response: " + packet.getUuid());
                    }
                });
            }

            @Override
            public void onReceive(NetworkHandler handler, IncomingPacket incomingPacket) {
                if (incomingPacket instanceof TestPacket) {
                    TestPacket packet = (TestPacket) incomingPacket;
                    System.out.println("Received Test Packet: " + packet.getUuid());
                }
            }

            @Override
            public void onDisconnect(NetworkHandler handler) {
                System.out.println("Client disconnected: " + handler.remoteAddress().toString());
                //server.stop();
            }

            @Override
            public void onError(NetworkHandler handler, Throwable cause) {
                //
            }
        });
        server.start();
        System.out.println("Server started");
        try {
            Thread.sleep(10 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
