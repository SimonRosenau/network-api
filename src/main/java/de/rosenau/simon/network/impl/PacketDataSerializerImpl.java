package de.rosenau.simon.network.impl;

import de.rosenau.simon.network.api.PacketDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    public boolean release() {
        return buffer.release();
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public void writeByte(byte b) {
        buffer.writeByte(b);
    }

    @Override
    public void readBytes(byte[] bytes) {
        buffer.readBytes(bytes);
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    @Override
    public void writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    @Override
    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    @Override
    public void writeBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    @Override
    public char readChar() {
        return buffer.readChar();
    }

    @Override
    public void writeChar(char c) {
        buffer.writeChar(c);
    }

    @Override
    public double readDouble() {
        return buffer.readDouble();
    }

    @Override
    public void writeDouble(double d) {
        buffer.writeDouble(d);
    }

    @Override
    public float readFloat() {
        return buffer.readFloat();
    }

    @Override
    public void writeFloat(float f) {
        buffer.writeFloat(f);
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public void writeInt(int i) {
        buffer.writeInt(i);
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public void writeLong(long l) {
        buffer.writeLong(l);
    }

    @Override
    public int readMedium() {
        return buffer.readMedium();
    }

    @Override
    public void writeMedium(int i) {
        buffer.writeMedium(i);
    }

    @Override
    public short readShort() {
        return buffer.readShort();
    }

    @Override
    public void writeShort(short s) {
        buffer.writeShort(s);
    }

    @Override
    public String readString() {
        byte[] bytes = readBytes(readInt());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String readNullableString() {
        if (readBoolean()) return null;
        return readString();
    }

    @Override
    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeBytes(bytes);
    }

    @Override
    public void writeNullableString(String string) {
        writeBoolean(string == null);
        if (string != null) writeString(string);
    }

    @Override
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    @Override
    public UUID readNullableUUID() {
        if (readBoolean()) return null;
        return readUUID();
    }

    @Override
    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public void writeNullableUUID(UUID uuid) {
        writeBoolean(uuid == null);
        if (uuid != null) writeUUID(uuid);
    }

}
