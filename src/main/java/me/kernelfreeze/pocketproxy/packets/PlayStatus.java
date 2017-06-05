package me.kernelfreeze.pocketproxy.packets;

import lombok.Getter;
import lombok.Setter;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class PlayStatus extends MCPEPacket {
    @Getter
    @Setter
    private Status status;

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

    public PlayStatus(Status status) {
        super(NetworkType.PLAY_STATUS_PACKET);
        this.status = status;
    }

    @Override
    public void encode() {
        writeInt(status.getId());
    }
}
