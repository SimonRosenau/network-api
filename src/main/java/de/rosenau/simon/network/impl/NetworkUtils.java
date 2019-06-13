package de.rosenau.simon.network.impl;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Project created by SIM0NSTR.
 */

class NetworkUtils {

    static EventLoopGroup getEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        }
        return new NioEventLoopGroup();
    }

    static Class<? extends ServerChannel> getServerChannelClass() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    static Class<? extends Channel> getChannelClass() {
        if (Epoll.isAvailable()) {
            return EpollSocketChannel.class;
        }
        return NioSocketChannel.class;
    }

}
