package me.kernelfreeze.pocketproxy.packets;

import io.netty.buffer.Unpooled;
import me.kernelfreeze.pocketproxy.Compression;
import me.kernelfreeze.pocketproxy.NetworkManager;
import me.kernelfreeze.pocketproxy.PocketProxy;
import me.kernelfreeze.pocketproxy.raknet.RakNetPacket;
import net.md_5.bungee.api.ProxyServer;

import java.util.zip.DataFormatException;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class LoginPacket extends DataPacket {
    public LoginPacket(RakNetPacket packet) {
        super(packet);
    }

    @Override
    public void decode() {
        player.protocolVersion = Math.toIntExact(readUIntLE());
        player.gameEdition = readUByte();

        if (!PocketProxy.isCompatible(player.getProtocolVersion())) {
            if (player.protocolVersion > PocketProxy.PROTOCOL) {
                NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_FAILED_SERVER));
                player.disconnect(ProxyServer.getInstance().getTranslation("outdated_server"));
            } else {
                NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT));
                player.disconnect(ProxyServer.getInstance().getTranslation("outdated_client"));
            }
            PocketProxy.getInstance().getLogger().info(String.format("Client from address %s tryied to login with protocol version %s", player.getSession().getAddress(), player.getProtocolVersion()));
            return;
        }

        byte[] payload = readBytes();

        try {
            Compression.inflate(Unpooled.wrappedBuffer(payload));
            player.chainData = readStringLE();
            player.skinData = readStringLE();
        } catch (DataFormatException | IndexOutOfBoundsException e) {
            throw new RuntimeException("Unable to inflate login data body", e);
        }

        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Logged in!!", "hola")
        );

        player.setLoggedIn(true);

        NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_SUCCESS));
        NetworkManager.sendPacket(player, new ServerHandshakePacket(getPlayer()));
    }
}
