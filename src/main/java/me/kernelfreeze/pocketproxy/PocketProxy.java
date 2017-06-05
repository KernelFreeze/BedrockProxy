package me.kernelfreeze.pocketproxy;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public final class PocketProxy extends Plugin {
    // Constants
    public static final int PROTOCOL = 113;
    public static final int PORT = 19132;
    public static final String VERSION = "1.1.0.55";

    @Getter
    private static PocketProxy instance;

    @Getter
    private NetworkManager networkManager;

    @Override
    public void onEnable() {
        instance = this;

        networkManager = new NetworkManager();
    }
}
