package me.kernelfreeze.pocketproxy.packets;

import lombok.Getter;
import lombok.Setter;

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
        super(NetworkType.DISCONNECT_PACKET);
    }

    @Override
    public void encode() {
        writeBoolean(hideDisconnectionScreen);

        if (!hideDisconnectionScreen) {
            writeString(message);
        }
    }
}
