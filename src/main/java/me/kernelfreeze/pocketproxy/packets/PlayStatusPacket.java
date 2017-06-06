package me.kernelfreeze.pocketproxy.packets;

import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.pocketproxy.PacketRegistry;
import me.kernelfreeze.pocketproxy.raknet.RakNetPacket;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class PlayStatusPacket extends DataPacket {
    @Getter
    @Setter
    private Status status;

    public PlayStatusPacket(Status status) {
        super(PacketRegistry.NetworkType.PLAY_STATUS_PACKET);
        this.status = status;
    }

    public PlayStatusPacket(RakNetPacket packet) {
        super(packet);
    }

    @Override
    public void encode() {
        writeInt(status.getId());
    }

    public enum Status {
        LOGIN_SUCCESS(0),
        LOGIN_FAILED_CLIENT(1),
        LOGIN_FAILED_SERVER(2),
        PLAYER_SPAWN(3),
        LOGIN_FAILED_INVALID_TENANT(4),
        LOGIN_FAILED_VANILLA_EDU(5),
        LOGIN_FAILED_EDU_VANILLA(6);

        @Getter
        private final int id;

        Status(int id) {
            this.id = id;
        }
    }
}
