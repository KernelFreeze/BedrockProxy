package me.kernelfreeze.pocketproxy;

import javax.crypto.KeyAgreement;
import java.security.*;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class Security {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] generateRandomToken() {
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return token;
    }

    public static byte[] getServerKey(KeyPair serverPair, byte[] token) throws InvalidKeyException {
        byte[] sharedSecret = getSharedSecret(serverPair, serverPair.getPublic());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(token);
        digest.update(sharedSecret);
        return digest.digest();
    }

    private static byte[] getSharedSecret(KeyPair serverPair, PublicKey clientKey) throws InvalidKeyException {
        KeyAgreement agreement;

        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        agreement.init(serverPair.getPrivate());
        agreement.doPhase(clientKey, true);
        return agreement.generateSecret();
    }
}
