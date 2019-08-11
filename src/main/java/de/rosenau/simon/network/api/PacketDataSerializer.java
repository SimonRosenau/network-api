package de.rosenau.simon.network.api;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 20:40
 */

public interface PacketDataSerializer {

    byte readByte();
    void writeByte(byte b);

    void readBytes(byte[] bytes);
    byte[] readBytes(int length);
    void writeBytes(byte[] bytes);

    boolean readBoolean();
    void writeBoolean(boolean b);

    char readChar();
    void writeChar(char c);

    double readDouble();
    void writeDouble(double d);

    float readFloat();
    void writeFloat(float f);

    int readInt();
    void writeInt(int i);

    long readLong();
    void writeLong(long l);

    int readMedium();
    void writeMedium(int i);

    short readShort();
    void writeShort(short s);

    String readString();

    String readNullableString();
    void writeString(String s);

    void writeNullableString(String s);

    UUID readUUID();

    UUID readNullableUUID();
    void writeUUID(UUID uuid);

    void writeNullableUUID(UUID uuid);

}
