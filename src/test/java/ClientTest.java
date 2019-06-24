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

        NetworkClient client = new ClientBuilder().host("localhost").port(3000).key("ffffffff").build();
        //client.setAutoReconnect(true);
        client.setListener(new NetworkListener() {
            @Override
            public void onConnect(NetworkHandler handler) {
                System.out.println("Connected to server");

                handler.registerIncomingPacket(0, TestPacket.class);
                handler.registerOutgoingPacket(0, TestPacket.class);
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
