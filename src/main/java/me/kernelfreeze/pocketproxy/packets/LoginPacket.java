package me.kernelfreeze.pocketproxy.packets;

import io.netty.buffer.Unpooled;
import me.kernelfreeze.pocketproxy.Compression;
import me.kernelfreeze.pocketproxy.PocketPlayer;
import me.kernelfreeze.pocketproxy.PocketProxy;
import net.marfgamer.jraknet.Packet;

import java.util.zip.DataFormatException;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class LoginPacket extends DataPacket {
    private final PocketPlayer player;

    public LoginPacket(PocketPlayer player, Packet packet) {
        super(packet, NetworkType.LOGIN_PACKET);
        this.player = player;
    }

    @Override
    public void decode() {
        player.protocolVersion = readInt();
        player.gameEdition = readUByte();

        if (!PocketProxy.isCompatible(player.getProtocolVersion())) {
            player.disconnect("You need to join with v" + PocketProxy.VERSION);
            return;
        }

        byte[] payload = readBytes();

        try {
            Compression.inflate(Unpooled.wrappedBuffer(payload));
            player.chainData = readStringLE();
            player.skinData = readStringLE();
            player.setLoggedIn(true);
        } catch (DataFormatException | IndexOutOfBoundsException e) {
            throw new RuntimeException("Unable to inflate login data body", e);
        }
        //sendPacket(player, new PlayStatus(PlayStatus.Status.LOGIN_SUCCESS));
        //sendPacket(player, new ServerToClientHandshake(PocketPlayer.getPlayer(session.getGloballyUniqueId())));
    }
}
