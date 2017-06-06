package me.kernelfreeze.pocketproxy;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public final class PocketProxy extends Plugin {
    // Constants
    public static final int PROTOCOL = 113;
    public static final int PORT = 19132;
    public static final String VERSION = "1.1.0.55";

    private static final int[] COMPATIBLE_PROTOCOL_VERSIONS = new int[]{PROTOCOL};

    @Getter
    private static PocketProxy instance;

    @Getter
    private PublicKey mojangPublicKey;

    @Getter
    private NetworkManager networkManager;

    public static boolean isCompatible(int protocolVersion) {
        return Arrays.binarySearch(COMPATIBLE_PROTOCOL_VERSIONS, protocolVersion) >= 0;
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            mojangPublicKey = KeyFactory.getInstance("EC").generatePublic(
                    new X509EncodedKeySpec(Base64
                            .getDecoder()
                            .decode("MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V")
                    )
            );
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        networkManager = new NetworkManager();
    }
}