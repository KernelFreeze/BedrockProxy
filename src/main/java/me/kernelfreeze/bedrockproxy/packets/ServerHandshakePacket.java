package me.kernelfreeze.bedrockproxy.packets;

import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.bedrockproxy.BedrockPlayer;
import me.kernelfreeze.bedrockproxy.PacketRegistry;
import me.kernelfreeze.bedrockproxy.Security;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class ServerHandshakePacket extends DataPacket {
    @Getter
    @Setter
    private KeyPair publicKey;

    @Getter
    @Setter
    private byte[] serverToken;

    public ServerHandshakePacket(BedrockPlayer p) {
        super(PacketRegistry.NetworkType.SERVER_TO_CLIENT_HANDSHAKE_PACKET);

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp384r1"));
            KeyPair serverKeyPair = generator.generateKeyPair();

            byte[] token = Security.generateRandomToken();
            byte[] serverKey = Security.getServerKey(serverKeyPair, token);
            p.enableEncryption(serverKey);

            publicKey = serverKeyPair;
            serverToken = token;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void encode() {
        writeString(Base64.getEncoder().encodeToString(publicKey.getPublic().getEncoded()));
        writeBytes(serverToken);
    }
}
