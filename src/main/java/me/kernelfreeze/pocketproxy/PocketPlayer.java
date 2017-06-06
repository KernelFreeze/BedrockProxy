package me.kernelfreeze.pocketproxy;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.marfgamer.jraknet.session.RakNetClientSession;

import java.util.HashMap;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class PocketPlayer {
    @Getter
    private static HashMap<Long, PocketPlayer> players = new HashMap<>();
    @Getter
    public int protocolVersion;
    @Getter
    public short gameEdition;
    @Getter
    public String chainData;
    @Getter
    public String skinData;
    @Getter
    private byte[] serverKey;
    @Getter
    @Setter
    private boolean loggedIn;
    @Getter
    private RakNetClientSession session;

    public static PocketPlayer getPlayer(long id) {
        if (players.containsKey(id)) {
            return players.get(id);
        } else {
            PocketPlayer p = new PocketPlayer();
            players.put(id, p);
            return p;
        }
    }

    public static PocketPlayer getPlayer(RakNetClientSession session) {
        long id = session.getGloballyUniqueId();

        if (players.containsKey(id)) {
            return players.get(id);
        } else {
            PocketPlayer p = new PocketPlayer();
            p.session = session;
            players.put(id, p);
            return p;
        }
    }

    public void enableEncryption(byte[] serverKey) {
        this.serverKey = serverKey;
    }

    public void disconnect(@NonNull String reason) {
        PocketProxy.getInstance().getNetworkManager().getServer().removeSession(getSession(), reason);
    }
}
