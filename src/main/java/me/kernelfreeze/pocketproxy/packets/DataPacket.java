package me.kernelfreeze.pocketproxy.packets;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.pocketproxy.PacketRegistry;
import me.kernelfreeze.pocketproxy.PocketPlayer;
import me.kernelfreeze.pocketproxy.raknet.Packet;
import me.kernelfreeze.pocketproxy.raknet.RakNetPacket;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class DataPacket extends RakNetPacket {
    @Getter
    @Setter
    protected PocketPlayer player;

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
        networkId = PacketRegistry.NetworkType.getById(readUByte());
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
            len = buffer().readableBytes() - buffer().readerIndex();
        }
        byte[] r = new byte[len];
        read(r);
        return r;
    }

    public void handle() {
        decode();
    }
}
