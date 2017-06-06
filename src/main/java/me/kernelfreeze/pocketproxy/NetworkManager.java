package me.kernelfreeze.pocketproxy;


import lombok.Getter;
import me.kernelfreeze.pocketproxy.packets.DataPacket;
import me.kernelfreeze.pocketproxy.packets.LoginPacket;
import net.marfgamer.jraknet.RakNetPacket;
import net.marfgamer.jraknet.identifier.MCPEIdentifier;
import net.marfgamer.jraknet.protocol.Reliability;
import net.marfgamer.jraknet.server.RakNetServer;
import net.marfgamer.jraknet.server.RakNetServerListener;
import net.marfgamer.jraknet.session.RakNetClientSession;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

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
        final ListenerInfo listenerInfo = ProxyServer.getInstance()
                .getConfig()
                .getListeners()
                .stream()
                .findFirst()
                .orElseGet(null);

        final int limit = (ProxyServer.getInstance().getConfig().getPlayerLimit() <= 0) ?
                Integer.MAX_VALUE : ProxyServer.getInstance().getConfig().getPlayerLimit();

        if (listenerInfo == null) {
            PocketProxy.getInstance().getLogger().severe("No listener found! :(");
            return;
        }

        MCPEIdentifier id = new MCPEIdentifier(
                ProxyServer.getInstance().getName(),
                PocketProxy.PROTOCOL,
                PocketProxy.VERSION,
                ProxyServer.getInstance().getOnlineCount(),
                listenerInfo.getMaxPlayers(),
                new Random().nextLong(),
                ProxyServer.getInstance().getName(),
                "Survival"
        );

        server = new RakNetServer(PocketProxy.PORT, limit, id);
        server.setListener(this);

        server.startThreaded();
        PocketProxy.getInstance().getLogger().info("Listening for MCPE clients at 0.0.0.0:" + PocketProxy.PORT);
    }

    public static void sendPacket(RakNetClientSession session, DataPacket packet) {
        session.sendMessage(Reliability.RELIABLE_SEQUENCED, packet);
    }

    public static void sendPacket(PocketPlayer player, DataPacket packet) {
        sendPacket(player.getSession(), packet);
    }

    @Override
    public void onClientConnect(RakNetClientSession session) {
        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Client has connected", session.getAddress())
        );
    }

    @Override
    public void onClientDisconnect(RakNetClientSession session, String reason) {
        PocketPlayer.getPlayers().remove(session.getGloballyUniqueId());
        PocketProxy.getInstance().getLogger().info(
                String.format("[%s] <-> Client has disconnected: %s", session.getAddress(), reason)
        );
    }

    @Override
    public void handleMessage(RakNetClientSession session, RakNetPacket pk, int channel) {
        // Check if is an Minecraft Packet
        if (pk.getId() != 0xFE) return;

        // Read the packet
        PocketPlayer p = PocketPlayer.getPlayer(session);

        if (p.isLoggedIn()) {
            DataPacket packet = new DataPacket(pk);

            switch (packet.getNetworkId()) {
                case UNKOWN:
                    pk.buffer().resetReaderIndex();
                    pk.readByte();
                    PocketProxy.getInstance().getLogger().info(
                            String.format("Unknown packet from %s: %s", session.getAddress(), Integer.toHexString(pk.readByte()).toUpperCase())
                    );
                    break;
            }
        } else {
            LoginPacket packet = new LoginPacket(p, pk);
            packet.decode();
        }
    }
}