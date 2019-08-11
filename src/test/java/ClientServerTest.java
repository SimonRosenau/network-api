import de.rosenau.simon.network.api.NetworkClient;
import de.rosenau.simon.network.api.NetworkHandler;
import de.rosenau.simon.network.api.NetworkListener;
import de.rosenau.simon.network.api.NetworkServer;
import de.rosenau.simon.network.impl.ClientBuilder;
import de.rosenau.simon.network.impl.ServerBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 11.08.2019
 * Time: 13:17
 */

public class ClientServerTest {

    @Test
    public void test() {
        NetworkServer server = new ServerBuilder().port(5555).key("Secret key").build();
        NetworkClient client = new ClientBuilder().port(5555).key("Secret key").host("localhost").build();

        CompletableFuture<Void> serverReceived = new CompletableFuture<>();
        CompletableFuture<Void> clientReceived = new CompletableFuture<>();

        server.setListener(new NetworkListener() {
            @Override
            public void onConnect(NetworkHandler handler) {
                handler.registerOutgoingPacket(1, TestPacket.class);
                handler.registerIncomingPacket(1, TestPacket.class);

                handler.sendPacket(new TestPacket("aaaa", "Test", UUID.randomUUID(), new Random().nextInt()), (handler1, packet) -> {
                    serverReceived.complete(null);
                });
            }

            @Override
            public void onDisconnect(NetworkHandler handler) {

            }

            @Override
            public void onError(NetworkHandler handler, Throwable cause) {

            }
        });
        client.setListener(new NetworkListener() {
            @Override
            public void onConnect(NetworkHandler handler) {
                handler.registerOutgoingPacket(1, TestPacket.class);
                handler.registerIncomingPacket(1, TestPacket.class);

                handler.sendPacket(new TestPacket("aaaa", "Test", UUID.randomUUID(), new Random().nextInt()), (handler1, packet) -> {
                    clientReceived.complete(null);
                });
            }

            @Override
            public void onDisconnect(NetworkHandler handler) {

            }

            @Override
            public void onError(NetworkHandler handler, Throwable cause) {

            }
        });

        server.start();
        client.setAutoReconnect(true);
        client.connect();

        try {
            serverReceived.get(10, TimeUnit.SECONDS);
            clientReceived.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Assert.fail(e.getMessage());
        } finally {
            client.disconnect();
            server.stop();
        }
    }

}
