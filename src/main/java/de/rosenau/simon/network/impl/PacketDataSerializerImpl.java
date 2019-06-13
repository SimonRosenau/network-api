package de.rosenau.simon.network.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import de.rosenau.simon.network.api.PacketDataSerializer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Project created by SIM0NSTR.
 */

class PacketDataSerializerImpl implements PacketDataSerializer {

    private ByteBuf buffer;

    PacketDataSerializerImpl(ByteBuf buffer) {
        this.buffer = buffer;
    }

    PacketDataSerializerImpl() {
        buffer = Unpooled.buffer();
    }

    byte[] toByteArray() {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(0, bytes);
        return bytes;
    }

    public int lenght() {
        return buffer.readableBytes();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public void writeByte(byte b) {
        buffer.writeByte(b);
    }

    public void readBytes(byte[] bytes) {
        buffer.readBytes(bytes);
    }

    public byte[] readBytes(int lenght) {
        byte[] bytes = new byte[lenght];
        buffer.readBytes(bytes);
        return bytes;
    }

    public void writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    public void writeBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    public char readChar() {
        return buffer.readChar();
    }

    public void writeChar(char c) {
        buffer.writeChar(c);
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public void writeDouble(double d) {
        buffer.writeDouble(d);
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public void writeFloat(float f) {
        buffer.writeFloat(f);
    }

    public int readInt() {
        return buffer.readInt();
    }

    public void writeInt(int i) {
        buffer.writeInt(i);
    }

    public long readLong() {
        return buffer.readLong();
    }

    public void writeLong(long l) {
        buffer.writeLong(l);
    }

    public int readMedium() {
        return buffer.readMedium();
    }

    public void writeMedium(int i) {
        buffer.writeMedium(i);
    }

    public short readShort() {
        return buffer.readShort();
    }

    public void writeShort(short s) {
        buffer.writeShort(s);
    }

    public String readString() {
        byte[] bytes = readBytes(readInt());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeBytes(bytes);
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

}
