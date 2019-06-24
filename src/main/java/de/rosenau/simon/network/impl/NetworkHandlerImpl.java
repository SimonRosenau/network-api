package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.*;
import de.rosenau.simon.network.exception.SendException;
import io.netty.channel.*;

import javax.naming.AuthenticationException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:16
 */

class NetworkHandlerImpl extends SimpleChannelInboundHandler<PacketDataSerializer> implements NetworkHandler {

    private NetworkInstanceImpl instance;
    private Channel channel;

    private HashMap<Integer, Class<? extends IncomingPacket>> incoming;
    private HashMap<Class<? extends OutgoingPacket>, Integer> outgoing;

    private Map<UUID, ResponseListener> responseListeners = new ConcurrentHashMap<>();
    private Map<IncomingPacket, UUID> replyPackets = new ConcurrentHashMap<>();

    private boolean authenticated = false;

    NetworkHandlerImpl(NetworkInstanceImpl instance, Channel channel) {
        this.instance = instance;
        this.channel = channel;
        this.incoming = new HashMap<>();
        this.outgoing = new HashMap<>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        authenticated = false;
        if (instance instanceof NetworkClientImpl) {
            PacketDataSerializer serializer = new PacketDataSerializerImpl();
            serializer.writeBoolean(instance.getKey() != null);
            if (instance.getKey() != null) serializer.writeString(instance.getKey());
            channel.writeAndFlush(serializer);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        authenticated = false;
        instance.onDisconnect(this);
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
        instance.listener.onError(this, cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, PacketDataSerializer packetDataSerializer) throws Exception {

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

        int id = packetDataSerializer.readInt();
        if (incoming.containsKey(id)) {
            IncomingPacket packet = incoming.get(id).newInstance();

            byte replyStatus = packetDataSerializer.readByte();
            if (replyStatus == 0) {
                packet.decode(packetDataSerializer);
                instance.listener.onReceive(this, packet);
            } else if (replyStatus == 1) {
                UUID uuid = packetDataSerializer.readUUID();
                replyPackets.put(packet, uuid);
                packet.decode(packetDataSerializer);
                instance.listener.onReceive(this, packet);
            } else if (replyStatus == 2) {
                UUID uuid = packetDataSerializer.readUUID();
                ResponseListener listener = responseListeners.remove(uuid);
                if (listener != null) {
                    packet.decode(packetDataSerializer);
                    listener.onResponse(this, packet);
                }
            }
        }
    }

    public void sendPacket(OutgoingPacket packet) {
        sendPacket(packet, null);
    }

    public void sendPacket(OutgoingPacket packet, ResponseListener listener) {
        Integer id = outgoing.get(packet.getClass());
        if (id == null) throw new SendException("Packet not registered: " + packet.getClass().getName());

        PacketDataSerializer serializer = new PacketDataSerializerImpl();
        serializer.writeInt(id);
        serializer.writeByte((byte) (listener == null ? 0 : 1));
        if (listener != null) {
            UUID uuid = UUID.randomUUID();
            serializer.writeUUID(uuid);
            responseListeners.put(uuid, listener);
        }
        packet.encode(serializer);
        channel.writeAndFlush(serializer);
    }

    public void reply(IncomingPacket incomingPacket, OutgoingPacket packet) {
        Integer id = outgoing.get(packet.getClass());
        if (id == null) throw new SendException("Packet not registered: " + packet.getClass().getName());

        UUID uuid = replyPackets.remove(incomingPacket);
        if (uuid == null) throw new SendException("Incoming packet didn't requested a response");

        PacketDataSerializer serializer = new PacketDataSerializerImpl();
        serializer.writeInt(id);
        serializer.writeByte((byte) 2);
        serializer.writeUUID(uuid);

        packet.encode(serializer);
        channel.writeAndFlush(serializer);
    }

    public void disconnect() {
        channel.close();
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public void registerIncomingPacket(int id, Class<? extends IncomingPacket> c) {
        incoming.put(id, c);
    }

    public void registerOutgoingPacket(int id, Class<? extends OutgoingPacket> c) {
        outgoing.put(c, id);
    }

}