package me.kernelfreeze.pocketproxy.packets;

import lombok.Getter;
import lombok.Setter;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class ServerToClientHandshakePacket extends MCPEPacket {
    @Getter
    @Setter
    private String publicKey;

    @Getter
    @Setter
    private String serverToken;

    public ServerToClientHandshakePacket(String publicKey, String serverToken) {
        super(NetworkType.SERVER_TO_CLIENT_HANDSHAKE_PACKET);

        this.publicKey = publicKey;
        this.serverToken = serverToken;
    }

    @Override
    public void encode() {
        writeString(publicKey);
        writeString(serverToken);
    }
}
