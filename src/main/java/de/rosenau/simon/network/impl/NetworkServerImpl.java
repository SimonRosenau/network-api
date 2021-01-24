package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.NetworkServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Project created by SIM0NSTR.
 */

class NetworkServerImpl extends NetworkInstanceImpl implements NetworkServer {

    private final int port;
    private final String key;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Channel channel;

    private final Map<String, Integer> blockedIps = new HashMap<>();

    NetworkServerImpl(int port, String key) {
        this.port = port;
        this.key = key;
    }

    @Override
    public void start() {
        bossGroup = NetworkUtils.getEventLoopGroup();
        workerGroup = NetworkUtils.getEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NetworkUtils.getServerChannelClass());
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                String hostString = ((InetSocketAddress) channel.remoteAddress()).getHostString();
                if (blockedIps.containsKey(hostString) && blockedIps.get(hostString) > 10) {
                    System.out.println(hostString + " was blocked due to too many false login attempts");
                    channel.close();
                    return;
                }
                channel.pipeline().addLast("decoder", new PacketDecoder());
                channel.pipeline().addLast("encoder", new PacketEncoder());
                channel.pipeline().addLast("handler", new NetworkHandlerImpl(NetworkServerImpl.this, channel));
            }
        });
        ChannelFuture future = bootstrap.bind(port);
        channel = future.channel();
        future.syncUninterruptibly();
    }

    @Override
    public void stop() {
        if (channel != null) channel.close().syncUninterruptibly();
        if (bossGroup != null) bossGroup.shutdownGracefully().syncUninterruptibly();
        if (workerGroup != null) workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    String getKey() {
        return key;
    }

    void addFailedLogin(String host) {
        blockedIps.putIfAbsent(host, 0);
        blockedIps.put(host, blockedIps.get(host) + 1);
    }

}
