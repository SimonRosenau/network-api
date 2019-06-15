package de.rosenau.simon.network.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import de.rosenau.simon.network.api.NetworkServer;

/**
 * Project created by SIM0NSTR.
 */

class NetworkServerImpl extends NetworkInstanceImpl implements NetworkServer {

    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Channel channel;

    NetworkServerImpl(int port) {
        this.port = port;
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
                channel.pipeline().addLast("decoder", new PacketDecoder());
                channel.pipeline().addLast("encoder", new PacketEncoder());
                channel.pipeline().addLast("handler", new NetworkHandlerImpl(NetworkServerImpl.this, channel));
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
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

}
