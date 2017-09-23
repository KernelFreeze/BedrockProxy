package me.kernelfreeze.bedrockproxy;

import lombok.Data;
import lombok.Getter;
import me.kernelfreeze.bedrockproxy.packets.DisconnectPacket;
import me.kernelfreeze.bedrockproxy.raknet.session.RakNetClientSession;

import java.util.HashMap;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public @Data
class BedrockPlayer {
    @Getter
    private static HashMap<Long, BedrockPlayer> players = new HashMap<>();

    private int protocolVersion;
    private short gameEdition;
    private String chainData;
    private String skinData;
    private byte[] serverKey;
    private RakNetClientSession session;

    private boolean loggedIn;

    public static BedrockPlayer getPlayer(long id) {
        if (players.containsKey(id)) {
            return players.get(id);
        } else {
            BedrockPlayer p = new BedrockPlayer();
            players.put(id, p);
            return p;
        }
    }

    public static BedrockPlayer getPlayer(RakNetClientSession session) {
        long id = session.getGloballyUniqueId();

        if (players.containsKey(id)) {
            return players.get(id);
        } else {
            BedrockPlayer p = new BedrockPlayer();
            p.session = session;
            players.put(id, p);
            return p;
        }
    }

    public void enableEncryption(byte[] serverKey) {
        this.serverKey = serverKey;
    }

    public void disconnect(String reason) {
        DisconnectPacket packet = new DisconnectPacket();
        if (reason != null) {
            packet.setHideDisconnectionScreen(false);
            packet.setMessage(reason);
            NetworkManager.sendPacket(getSession(), packet);

            BedrockProxy.getInstance().getNetworkManager().getServer().removeSession(getSession(), reason);
        } else {
            packet.setHideDisconnectionScreen(true);
            NetworkManager.sendPacket(getSession(), packet);
            BedrockProxy.getInstance().getNetworkManager().getServer().removeSession(getSession());
        }
    }
}
