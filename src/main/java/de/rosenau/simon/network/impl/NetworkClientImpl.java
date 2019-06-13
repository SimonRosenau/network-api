package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.NetworkClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.util.concurrent.TimeUnit;

/**
 * Project created by SIM0NSTR.
 */

class NetworkClientImpl extends NetworkInstanceImpl implements NetworkClient {

    private String host;
    private int port;

    private boolean keepAlive;

    private EventLoopGroup eventLoopGroup;
    private Channel channel;

    NetworkClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    public boolean isKeepAlive() {
        return keepAlive;
    }

    @Override
    public void connect() {
        if (eventLoopGroup == null) eventLoopGroup = NetworkUtils.getEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NetworkUtils.getChannelClass());
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast("decoder", new PacketDecoder());
                channel.pipeline().addLast("encoder", new PacketEncoder());
                channel.pipeline().addLast("handler", new NetworkHandlerImpl(NetworkClientImpl.this, channel));
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess() && isKeepAlive()) {
                EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(this::connect, 1, TimeUnit.SECONDS);
            }
        });
        channel = future.channel();
        future.syncUninterruptibly();
    }

    @Override
    public void disconnect() {
        if (channel != null) channel.close().syncUninterruptibly();
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
            eventLoopGroup = null;
        }
    }

}
