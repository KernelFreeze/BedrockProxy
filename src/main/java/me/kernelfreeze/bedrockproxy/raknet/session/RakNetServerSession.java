/*
 *       _   _____            _      _   _          _   
 *      | | |  __ \          | |    | \ | |        | |  
 *      | | | |__) |   __ _  | | __ |  \| |   ___  | |_ 
 *  _   | | |  _  /   / _` | | |/ / | . ` |  / _ \ | __|
 * | |__| | | | \ \  | (_| | |   <  | |\  | |  __/ | |_ 
 *  \____/  |_|  \_\  \__,_| |_|\_\ |_| \_|  \___|  \__|
 *                                                  
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2017 MarfGamer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 */
package me.kernelfreeze.bedrockproxy.raknet.session;

import io.netty.channel.Channel;
import me.kernelfreeze.bedrockproxy.raknet.RakNetPacket;
import me.kernelfreeze.bedrockproxy.raknet.client.RakNetClient;
import me.kernelfreeze.bedrockproxy.raknet.protocol.Reliability;
import me.kernelfreeze.bedrockproxy.raknet.protocol.login.ConnectionRequestAccepted;
import me.kernelfreeze.bedrockproxy.raknet.protocol.login.NewIncomingConnection;
import me.kernelfreeze.bedrockproxy.raknet.protocol.message.acknowledge.Record;

import java.net.InetSocketAddress;

import static me.kernelfreeze.bedrockproxy.raknet.protocol.MessageIdentifier.*;

/**
 * This class represents a server connection and handles the login sequence
 * packets.
 *
 * @author MarfGamer
 */
public class RakNetServerSession extends RakNetSession {

    private final RakNetClient client;

    /**
     * Constructs a <code>RakNetClientSession</code> with the specified
     * <code>RakNetClient</code>, globally unique ID, maximum transfer unit,
     * <code>Channel</code>, and address.
     *
     * @param client              the <code>RakNetClient</code>.
     * @param guid                the globally unique ID.
     * @param maximumTransferUnit the maximum transfer unit
     * @param channel             the <code>Channel</code>.
     * @param address             the address.
     */
    public RakNetServerSession(RakNetClient client, long guid, int maximumTransferUnit, Channel channel,
                               InetSocketAddress address) {
        super(guid, maximumTransferUnit, channel, address);
        this.client = client;
        this.setState(RakNetState.HANDSHAKING); // We start at the handshake
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAcknowledge(Record record) {
        client.getListener().onAcknowledge(this, record);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onNotAcknowledge(Record record) {
        client.getListener().onNotAcknowledge(this, record);
    }

    @Override
    public void handleMessage(RakNetPacket packet, int channel) {
        short packetId = packet.getId();

        if (packetId == ID_CONNECTION_REQUEST_ACCEPTED && this.getState() == RakNetState.HANDSHAKING) {
            ConnectionRequestAccepted serverHandshake = new ConnectionRequestAccepted(packet);
            serverHandshake.decode();

            if (!serverHandshake.failed()) {
                NewIncomingConnection clientHandshake = new NewIncomingConnection();
                clientHandshake.serverAddress = client.getSession().getAddress();
                clientHandshake.clientTimestamp = serverHandshake.clientTimestamp;
                clientHandshake.serverTimestamp = serverHandshake.serverTimestamp;
                clientHandshake.encode();

                if (!clientHandshake.failed()) {
                    this.sendMessage(Reliability.RELIABLE, clientHandshake);
                    this.setState(RakNetState.CONNECTED);
                    client.getListener().onConnect(this);
                } else {
                    client.disconnect("Failed to login");
                }
            } else {
                client.disconnect("Failed to login");
            }
        } else if (packetId == ID_DISCONNECTION_NOTIFICATION) {
            this.setState(RakNetState.DISCONNECTED);
            client.disconnect("Server disconnected");
        } else if (packetId >= ID_USER_PACKET_ENUM) {
            client.getListener().handleMessage(this, packet, channel);
        }
    }

    /**
     * Called by the client when the connection is closed.
     */
    public void closeConnection() {
        this.sendMessage(Reliability.UNRELIABLE, ID_DISCONNECTION_NOTIFICATION);
    }

}
