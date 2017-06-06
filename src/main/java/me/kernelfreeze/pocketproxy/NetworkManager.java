package me.kernelfreeze.pocketproxy;


import lombok.Getter;
import me.kernelfreeze.pocketproxy.packets.DataPacket;
import me.kernelfreeze.pocketproxy.raknet.RakNetPacket;
import me.kernelfreeze.pocketproxy.raknet.identifier.MCPEIdentifier;
import me.kernelfreeze.pocketproxy.raknet.protocol.Reliability;
import me.kernelfreeze.pocketproxy.raknet.server.RakNetServer;
import me.kernelfreeze.pocketproxy.raknet.server.RakNetServerListener;
import me.kernelfreeze.pocketproxy.raknet.session.RakNetClientSession;
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
        PacketRegistry.handlePacket(pk, PocketPlayer.getPlayer(session));
    }
}