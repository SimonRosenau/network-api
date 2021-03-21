package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.*;
import de.rosenau.simon.network.exception.ReceiveException;
import de.rosenau.simon.network.exception.SendException;
import io.netty.channel.*;

import javax.naming.AuthenticationException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:16
 */

class NetworkHandlerImpl extends SimpleChannelInboundHandler<PacketDataSerializer> implements NetworkHandler {

    private final NetworkInstanceImpl instance;
    private final Channel channel;

    private final HashMap<Integer, Class<? extends IncomingPacket>> incomingPackets = new HashMap<>();
    private final HashMap<Class<? extends OutgoingPacket>, Integer> outgoingPackets = new HashMap<>();
    private final HashMap<Integer, Class<? extends IncomingRequest<?>>> incomingRequests = new HashMap<>();
    private final HashMap<Class<? extends OutgoingRequest<?>>, Integer> outgoingRequests = new HashMap<>();
    private final HashMap<Class<? extends OutgoingRequest<?>>, Class<? extends IncomingResponse>> requestResponses = new HashMap<>();

    private final Map<UUID, ResponseListener<?>> responseListeners = new ConcurrentHashMap<>();

    private Thread workerThread = null;
    private boolean authenticated = false;

    NetworkHandlerImpl(NetworkInstanceImpl instance, Channel channel) {
        this.instance = instance;
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        debug("Channel active");
        workerThread = Thread.currentThread();
        authenticated = false;
        if (instance instanceof NetworkClientImpl) {
            PacketDataSerializer serializer = new PacketDataSerializerImpl();
            serializer.writeBoolean(instance.getKey() != null);
            if (instance.getKey() != null) serializer.writeString(instance.getKey());
            channel.writeAndFlush(serializer);
            if (instance.getKey() != null) debug("Sent authorization key");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        debug("Channel inactive");
        if (authenticated) instance.onDisconnect(this);
        workerThread = null;
        authenticated = false;

        responseListeners.values().forEach(responseListener -> responseListener.onResponse(null, null, new ReceiveException("Handler disconnected")));
        responseListeners.clear();

        if (instance instanceof NetworkClientImpl) {
            NetworkClientImpl client = ((NetworkClientImpl) instance);
            if (client.isAutoReconnect()) {
                EventLoop loop = ctx.channel().eventLoop();
                loop.schedule(client::connect, 1, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (instance.isDebug()) cause.printStackTrace();
        instance.listener.onError(this, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PacketDataSerializer packetDataSerializer) {
        try {
            if (!authenticated) {
                if (instance instanceof NetworkServerImpl) {
                    boolean authentication = packetDataSerializer.readBoolean();
                    if (instance.getKey() != null && authentication) {
                        String key = packetDataSerializer.readString();
                        boolean authenticated = instance.getKey().equals(key);
                        PacketDataSerializer serializer = new PacketDataSerializerImpl();
                        serializer.writeBoolean(authenticated);

                        if (!authenticated) {
                            // Wrong key sent
                            ((NetworkServerImpl) instance).addFailedLogin(((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getHostString());
                            channel.writeAndFlush(serializer).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            channel.writeAndFlush(serializer).addListener((ChannelFutureListener) future -> instance.onConnect(this));
                            this.authenticated = true;
                        }
                    } else if (instance.getKey() != null) {
                        // No key sent
                        PacketDataSerializer serializer = new PacketDataSerializerImpl();
                        serializer.writeBoolean(false);
                        ((NetworkServerImpl) instance).addFailedLogin(((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getHostString());
                        channel.writeAndFlush(serializer).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        PacketDataSerializer serializer = new PacketDataSerializerImpl();
                        serializer.writeBoolean(true);
                        channel.writeAndFlush(serializer).addListener((ChannelFutureListener) future -> instance.onConnect(this));
                        this.authenticated = true;
                    }
                } else if (instance instanceof NetworkClientImpl) {
                    boolean authenticated = packetDataSerializer.readBoolean();
                    if (authenticated) {
                        instance.onConnect(this);
                        this.authenticated = true;
                    } else {
                        throw new AuthenticationException("Unable to authenticate with remote host");
                    }
                }
                return;
            }

            try {
                byte type = packetDataSerializer.readByte();
                debug("Received packet of type: " + type);

                if (type == 0) {
                    int id = packetDataSerializer.readInt();
                    debug("Received packet of id: " + id);
                    if (incomingPackets.containsKey(id)) {
                        IncomingPacket packet = incomingPackets.get(id).getConstructor().newInstance();
                        debug("Received packet is of class: " + packet.getClass().getName());
                        packet.decode(packetDataSerializer);
                        debug("Decoded packet: " + packet.getClass().getName());
                        channel.eventLoop().submit(() -> {
                            packet.handle(this);
                            debug("Handled packet: " + packet.getClass().getName());
                        });
                    }
                } else if (type == 1) {
                    int id = packetDataSerializer.readInt();
                    if (incomingRequests.containsKey(id)) {
                        IncomingRequest<?> request = incomingRequests.get(id).getConstructor().newInstance();
                        UUID responseKey = packetDataSerializer.readUUID();
                        request.decode(packetDataSerializer);
                        channel.eventLoop().submit(() -> {
                            try {
                                OutgoingResponse response = request.handle(this);
                                respond(response, responseKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else if (type == 2) {
                    UUID responseKey = packetDataSerializer.readUUID();
                    @SuppressWarnings("unchecked")
                    ResponseListener<IncomingResponse> listener = (ResponseListener<IncomingResponse>) responseListeners.remove(responseKey);
                    if (listener != null) {
                        IncomingResponse response = listener.createResponsePacket();
                        response.decode(packetDataSerializer);
                        channel.eventLoop().submit(() -> {
                            try {
                                listener.onResponse(this, response, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                if (instance.isDebug()) e.printStackTrace();
                instance.listener.onError(this, e);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            ((PacketDataSerializerImpl) packetDataSerializer).release();
        }
    }

    @Override
    public void send(OutgoingPacket packet) {
        Integer id = outgoingPackets.get(packet.getClass());
        if (id == null) throw new SendException("Packet not registered: " + packet.getClass().getName());

        PacketDataSerializer serializer = new PacketDataSerializerImpl();
        serializer.writeByte((byte) 0);
        serializer.writeInt(id);
        packet.encode(serializer);
        channel.writeAndFlush(serializer);
        debug("Sent packet: " + id + " - " + packet.getClass().getName());
    }

    @Override
    public <T extends IncomingResponse> T request(OutgoingRequest<T> request) throws RuntimeException {
        if (workerThread != null && Thread.currentThread().getName().equals(workerThread.getName())) throw new RuntimeException("Cannot request packet in network thread");
        Integer id = outgoingRequests.get(request.getClass());
        if (id == null) throw new SendException("Request not registered: " + request.getClass().getName());

        UUID responseKey = UUID.randomUUID();

        PacketDataSerializer serializer = new PacketDataSerializerImpl();
        serializer.writeByte((byte) 1);
        serializer.writeInt(id);
        serializer.writeUUID(responseKey);
        request.encode(serializer);

        CompletableFuture<T> future = new CompletableFuture<>();
        responseListeners.put(responseKey, new ResponseListener<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T createResponsePacket() throws Exception {
                return (T) requestResponses.get(request.getClass()).getConstructor().newInstance();
            }

            @Override
            public void onResponse(NetworkHandler handler, T response, Throwable throwable) {
                if (response != null) future.complete(response);
                if (throwable != null) future.completeExceptionally(throwable);
            }
        });

        channel.writeAndFlush(serializer);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to get network response", e);
        }
    }

    private void respond(OutgoingResponse packet, UUID responseKey) {
        PacketDataSerializer serializer = new PacketDataSerializerImpl();
        serializer.writeByte((byte) 2);
        serializer.writeUUID(responseKey);
        packet.encode(serializer);
        channel.writeAndFlush(serializer);
    }

    public void disconnect() {
        channel.close();
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public void registerIncomingPacket(int id, Class<? extends IncomingPacket> c) {
        incomingPackets.put(id, c);
    }

    @Override
    public void registerOutgoingPacket(int id, Class<? extends OutgoingPacket> c) {
        outgoingPackets.put(c, id);
    }

    @Override
    public void registerIncomingRequest(int id, Class<? extends IncomingRequest<? extends OutgoingResponse>> c) {
        incomingRequests.put(id, c);
    }

    @Override
    public <T extends IncomingResponse> void registerOutgoingRequest(int id, Class<? extends OutgoingRequest<T>> c, Class<T> rc) {
        outgoingRequests.put(c, id);
        requestResponses.put(c, rc);
    }

    private interface ResponseListener <T extends IncomingResponse> {
        T createResponsePacket() throws Exception;
        void onResponse(NetworkHandler handler, T packet, Throwable throwable);
    }

    private void debug(String message) {
        if (!instance.isDebug()) return;
        System.out.println("[DEBUG] " + message);
    }

}