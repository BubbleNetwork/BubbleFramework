package com.thebubblenetwork.api.framework.util.plugindownloader;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import org.bukkit.plugin.Plugin;
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
            DownloadUtil.download(jar, pluginURL, BubbleNetwork.getInstance().getFileConnection());
        } catch (Exception e) {
            network.getLogger().log(Level.WARNING, "Could not download UltraCosmetics");
        }
    }

    public void load() {
        if (!jar.exists()) {
            throw new IllegalArgumentException("Jar doesn't exist");
        }
        try {
            plugin = network.getPlugman().load(jar);
        }
        catch (Exception ex){
            getNetwork().getLogger().log(Level.WARNING, "Could not load " + jar + " ", ex);
            for(Plugin plugin: network.getPlugin().getServer().getPluginManager().getPlugins()){
                if(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile().equals(jar)){
                    this.plugin = (JavaPlugin) plugin;
                    break;
                }
            }
        }
    }

    public void enable() {
        try {
            network.getPlugman().enable(plugin);
        } catch (Exception ex) {
            getNetwork().getLogger().log(Level.WARNING, "Could not enable " + jar + " ", ex);
        }
    }

    public void disable() {
        try {
            network.getPlugman().disable(plugin);
        } catch (Exception ex) {
            getNetwork().getLogger().log(Level.WARNING, "Could not disable " + jar + " ", ex);
        }
    }

    public void unload() {
        try {
            network.getPlugman().unload(plugin);
        } catch (Exception ex) {
            getNetwork().getLogger().log(Level.WARNING, "Could not unload " + jar + " ", ex);
        }
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
