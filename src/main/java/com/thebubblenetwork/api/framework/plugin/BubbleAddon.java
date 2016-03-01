package com.thebubblenetwork.api.framework.plugin;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.P;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.ServerShutdownRequest;
import com.thebubblenetwork.api.global.plugin.updater.FileUpdater;
import de.mickare.xserver.net.XServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Jacob on 09/12/2015.
 */
public abstract class BubbleAddon implements FileUpdater {
    private AddonDescriptionFile descriptionFile;
    private File file;
    private BubbleAddonLoader loader;

    public BubbleAddon() {
        BubbleNetwork.getInstance().addUpdater(this);
    }

    public org.bukkit.plugin.PluginDescriptionFile getDescription() {
        return AddonDescriptionFile.asMirror(descriptionFile);
    }

    public AddonDescriptionFile getDescriptionBubble() {
        return descriptionFile;
    }

    public boolean isEnabled() {
        return getPlugin().isEnabled();
    }

    public void __init__(BubbleAddonLoader loader) {
        this.loader = loader;
        file = loader.getJar();
        descriptionFile = loader.getFile();
    }

    public BubbleAddonLoader getLoader() {
        return loader;
    }

    public void onLoad() {
    }


    public void onEnable() {

    }

    public void onDisable() {

    }

    public P getPlugin() {
        return BubbleNetwork.getInstance().getPlugin();
    }

    public abstract long finishUp();

    public File getDataFolder() {
        return new File(getPlugin().getDataFolder() + File.separator + getName());
    }

    public Server getServer() {
        return getPlugin().getServer();
    }

    public String getName() {
        return descriptionFile.getName();
    }

    public BukkitTask runTask(Runnable r) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, TimeUnit.MILLISECONDS, 0L, false, false);
    }

    public BukkitTask runTaskAsynchonrously(Runnable r) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, TimeUnit.MILLISECONDS, 0L, false, true);
    }

    public BukkitTask runTaskLater(Runnable r, TimeUnit unit, long time) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, unit, time, false, false);
    }

    public BukkitTask runTaskLaterAsynchronously(Runnable r, TimeUnit unit, long time) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, unit, time, false, true);
    }

    public BukkitTask runTaskTimer(Runnable r, TimeUnit unit, long time) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, unit, time, true, false);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable r, TimeUnit unit, long time) {
        return BubbleNetwork.getInstance().registerRunnable(this, r, unit, time, true, true);
    }

    public void registerListener(Listener l) {
        BubbleNetwork.getInstance().registerListener(this, l);
    }

    public String getArtifact() {
        return getName();
    }

    public File getReplace() {
        return file;
    }

    public void updateTaskAfter() {
        XServer proxy = BubbleNetwork.getInstance().getProxy();
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(proxy, new ServerShutdownRequest());
        } catch (IOException e) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not disconnect from proxy", e);
            Bukkit.shutdown();
            return;
        }
        try {
            proxy.disconnect();
        } catch (Exception e) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not disconnect from proxy", e);
            Bukkit.shutdown();
            return;
        }
        try {
            proxy.connect();
        } catch (Exception e) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not connect to proxy", e);
            Bukkit.shutdown();
        }
    }

    public void updateTaskBefore() {
        BubbleNetwork network = BubbleNetwork.getInstance();
        network.disableAddon();
    }
}
