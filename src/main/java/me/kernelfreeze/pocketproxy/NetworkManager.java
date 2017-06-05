package me.kernelfreeze.pocketproxy;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import me.kernelfreeze.pocketproxy.packets.MCPEPacket;
import me.kernelfreeze.pocketproxy.packets.PlayStatus;
import net.marfgamer.jraknet.Packet;
import net.marfgamer.jraknet.RakNetPacket;
import net.marfgamer.jraknet.identifier.MCPEIdentifier;
import net.marfgamer.jraknet.protocol.MessageIdentifier;
import net.marfgamer.jraknet.protocol.Reliability;
import net.marfgamer.jraknet.protocol.message.EncapsulatedPacket;
import net.marfgamer.jraknet.server.RakNetServer;
import net.marfgamer.jraknet.server.RakNetServerListener;
import net.marfgamer.jraknet.session.RakNetClientSession;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * @author KernelFreeze
 * @since 4/06/17
 */
public class NetworkManager implements RakNetServerListener {
    @Getter
    private RakNetServer server;

    public NetworkManager() {
        // Get max players from the first listener found
        final ListenerInfo listenerInfo = ProxyServer.getInstance().getConfig().getListeners().stream().findFirst().orElseGet(null);

        final int limit = (ProxyServer.getInstance().getConfig().getPlayerLimit() < 0) ?
                Integer.MAX_VALUE : ProxyServer.getInstance().getConfig().getPlayerLimit();

        if (listenerInfo == null) {
            PocketProxy.getInstance().getLogger().severe("No listener found on the config file!");
            return;
        }

        MCPEIdentifier id = new MCPEIdentifier(
                ProxyServer.getInstance().getName(),
                PocketProxy.PROTOCOL,
                PocketProxy.VERSION,
                ProxyServer.getInstance().getOnlineCount(),
                listenerInfo.getMaxPlayers(),
                new Random().nextLong(),
                listenerInfo.getMotd(),
                "Survival"
        );

        server = new RakNetServer(PocketProxy.PORT, limit, id);
        server.setListener(this);

        server.startThreaded();
        PocketProxy.getInstance().getLogger().info("Listening for MCPE clients at 0.0.0.0:" + PocketProxy.PORT);
    }

    @Override
    public void onClientConnect(RakNetClientSession session) {
        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Client has connected", session.getAddress())
        );
        session.sendMessage(Reliability.RELIABLE_SEQUENCED, new PlayStatus(PlayStatus.Status.LOGIN_SUCCESS));
    }

    @Override
    public void onClientDisconnect(RakNetClientSession session, String reason) {
        session.getGloballyUniqueId();
        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Client has disconnected: %s", session.getAddress(), reason)
        );
    }


    @Override
    public void handleMessage(RakNetClientSession session, RakNetPacket packet, int channel) {
        // Check if is an Minecraft Packet
        if (packet.getId() != 0xFE) return;

        // Read the packet
        short id = packet.readUByte();

        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Packet: %s", session.getAddress(), Integer.toHexString(id).toUpperCase())
        );
        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> %d %d", session.getAddress(), packet.readUInt(), packet.readByte())
        );
    }
}
