package com.thebubblenetwork.api.framework.cosmetics;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.plugindownloader.AbstractPluginDownloader;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.FileUTIL;
import com.thebubblenetwork.api.global.file.SSLUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class CosmeticsManager extends AbstractPluginDownloader{
    private static final String PluginURL = "UltraCosmetics.jar";

    public CosmeticsManager(BubbleNetwork network) {
        super(network, PluginURL, new File(network.getPlugin().getDataFolder(), "UltraCosmetics.jar"));
    }

    public Unsafe unsafe() {
        return new Unsafe() {
            public CosmeticsHook create() {
                if (getPlugin() == null || !getPlugin().isEnabled()) {
                    throw new IllegalArgumentException("Can only hook when enabled");
                }
                return new CosmeticsHook(getPlugin());
            }
        };
    }

    public interface Unsafe {
        CosmeticsHook create();
    }
}
