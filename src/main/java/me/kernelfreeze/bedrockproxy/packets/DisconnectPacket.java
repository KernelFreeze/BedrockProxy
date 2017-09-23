package me.kernelfreeze.bedrockproxy.packets;

import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.bedrockproxy.PacketRegistry;
import me.kernelfreeze.bedrockproxy.raknet.RakNetPacket;

/**
 * @author KernelFreeze
 * @since 6/06/17
 */
@Getter
@Setter
public class DisconnectPacket extends DataPacket {
    private boolean hideDisconnectionScreen;
    private String message;

    public DisconnectPacket() {
        super(PacketRegistry.NetworkType.DISCONNECT_PACKET);
    }

    public DisconnectPacket(RakNetPacket packet) {
        super(packet);
    }

    @Override
    public void encode() {
        writeBoolean(hideDisconnectionScreen);

        if (!hideDisconnectionScreen) {
            writeString(message);
        }
    }
}
