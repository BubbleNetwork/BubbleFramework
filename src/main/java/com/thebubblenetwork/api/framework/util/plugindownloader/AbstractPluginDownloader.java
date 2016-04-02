package com.thebubblenetwork.api.framework.util.plugindownloader;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.SSLUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class AbstractPluginDownloader {
    private final File jar;
    private String pluginURL;
    private BubbleNetwork network;
    private JavaPlugin plugin = null;

    public AbstractPluginDownloader(BubbleNetwork network, String pluginURL, File jar) {
        this.network = network;
        this.jar = jar;
        this.pluginURL = pluginURL;
    }

    public File getJar() {
        return jar;
    }

    public void download() {
        try {
            SSLUtil.allowAnySSL();
        } catch (Exception e) {
            network.getLogger().log(Level.WARNING, "Could not allow any SSL", e);
        }
        try {
            DownloadUtil.download(jar, pluginURL, BubbleNetwork.getInstance().getFileConnection());
        } catch (Exception e) {
            network.getLogger().log(Level.WARNING, "Could not download UltraCosmetics");
        }
    }

    public void load() {
        if (!jar.exists()) {
            throw new IllegalArgumentException("Jar doesn't exist");
        }
        plugin = network.getPlugman().load(jar);
    }

    public void enable() {
        network.getPlugman().enable(plugin);
    }

    public void disable() {
        network.getPlugman().disable(plugin);
    }

    public void unload() {
        network.getPlugman().unload(plugin);
        plugin = null;
    }

    public void clearUp() {
        jar.delete();
    }

    public JavaPlugin getPlugin(){
        return plugin;
    }

    public BubbleNetwork getNetwork(){
        return network;
    }
}
