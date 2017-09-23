package me.kernelfreeze.bedrockproxy.packets;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.bedrockproxy.BedrockPlayer;
import me.kernelfreeze.bedrockproxy.PacketRegistry;
import me.kernelfreeze.bedrockproxy.raknet.Packet;
import me.kernelfreeze.bedrockproxy.raknet.RakNetPacket;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class DataPacket extends RakNetPacket {
    @Getter
    @Setter
    protected BedrockPlayer player;

    @Getter
    protected PacketRegistry.NetworkType networkId;

    public DataPacket(RakNetPacket packet) {
        super(packet);
        networkId = PacketRegistry.NetworkType.getById(packet.getId());
    }

    public DataPacket(Packet packet, PacketRegistry.NetworkType id) {
        super(packet);
        networkId = id;
    }

    public DataPacket(ByteBuf data) {
        super(data);
        networkId = PacketRegistry.NetworkType.getById(getId());
    }

    public DataPacket(PacketRegistry.NetworkType id) {
        super(id.getId());
        networkId = id;
    }

    public void writeBytes(byte[] bytes) {
        writeUInt(bytes.length);
        for (byte b : bytes) {
            writeByte(b);
        }
    }

    public byte[] readBytes() {
        int len = readUShort();
        if (buffer().readerIndex() + len > buffer().readableBytes()) {
            throw new RuntimeException("I can't read " + (buffer().readableBytes() - buffer().readerIndex() + len) + " bytes!");
        }
        byte[] r = new byte[len];
        read(r);
        return r;
    }

    public void skip(int bytes) {
        buffer().readerIndex(buffer().readerIndex() + bytes);
    }

    public void handle() {
        decode();
    }
}
