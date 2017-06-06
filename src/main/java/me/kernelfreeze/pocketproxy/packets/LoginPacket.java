package me.kernelfreeze.pocketproxy.packets;

import me.kernelfreeze.pocketproxy.NetworkManager;
import me.kernelfreeze.pocketproxy.PocketProxy;
import me.kernelfreeze.pocketproxy.raknet.RakNetPacket;
import net.md_5.bungee.api.ProxyServer;

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
        if (player.isLoggedIn()) return;
        buffer().readerIndex(1);

        player.gameEdition = readUByte();
        player.protocolVersion = readUShortLE();

        if (!PocketProxy.isCompatible(player.getProtocolVersion())) {
            if (player.protocolVersion > PocketProxy.PROTOCOL) {
                NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_FAILED_SERVER));
                player.disconnect(ProxyServer.getInstance().getTranslation("outdated_server"));
            } else {
                NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT));
                player.disconnect(ProxyServer.getInstance().getTranslation("outdated_client"));
            }
            PocketProxy.getInstance().getLogger().info(String.format("Client from address %s tryied to login with protocol version %s", player.getSession().getAddress(), player.getProtocolVersion()));
            return; //Do not attempt to decode for non-accepted protocols
        }
        skip(3);

        player.chainData = readString();

        //player.skinData = readStringLE();

        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Logged in!!", "hola")
        );

        player.setLoggedIn(true);

        NetworkManager.sendPacket(player, new PlayStatusPacket(PlayStatusPacket.Status.LOGIN_SUCCESS));
        NetworkManager.sendPacket(player, new ServerHandshakePacket(getPlayer()));
    }
}
