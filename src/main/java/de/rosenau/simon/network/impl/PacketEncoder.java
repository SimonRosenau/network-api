package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.PacketDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Project created by SIM0NSTR.
 */

class PacketEncoder extends MessageToByteEncoder<PacketDataSerializer> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, PacketDataSerializer packet, ByteBuf byteBuf) {
        byte[] bytes = ((PacketDataSerializerImpl) packet).toByteArray();
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

}
