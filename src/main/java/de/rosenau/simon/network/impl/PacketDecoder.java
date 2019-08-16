package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.PacketDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Project created by SIM0NSTR.
 */

class PacketDecoder extends ByteToMessageDecoder {

    private ByteBuf buffer;
    private int length;

    PacketDecoder() {
        buffer = Unpooled.buffer();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() == 0) return;
        buffer.ensureWritable(byteBuf.readableBytes());
        byteBuf.readBytes(buffer, byteBuf.readableBytes());
        while(true) {
            PacketDataSerializer packet = readPacket();
            if (packet == null) break;
            list.add(packet);
        }
    }

    private PacketDataSerializer readPacket() {
        if (length == 0 && buffer.readableBytes() >= 4) {
            length = buffer.readInt();
        }
        if (length != 0 && buffer.readableBytes() >= length) {
            ByteBuf packet = Unpooled.buffer(length);
            buffer.readBytes(packet, length);
            buffer.discardReadBytes();
            length = 0;
            return new PacketDataSerializerImpl(packet);
        }
        return null;
    }

}
