package com.thebubblenetwork.api.framework.plugin;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.P;
import com.thebubblenetwork.api.global.plugin.updater.FileUpdater;
import org.bukkit.Server;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jacob on 09/12/2015.
 */
public abstract class BubbleAddon implements FileUpdater {
    private AddonDescriptionFile descriptionFile;
    private File file;
    private BubbleAddonLoader loader;

    public BubbleAddon(){
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

    public void runTask(Runnable r){
        BubbleNetwork.getInstance().registerRunnable(this,r, TimeUnit.MILLISECONDS,0L,false,false);
    }

    public void runTaskAsynchonrously(Runnable r){
        BubbleNetwork.getInstance().registerRunnable(this,r, TimeUnit.MILLISECONDS,0L,false,true);
    }

    public void runTaskLater(Runnable r,TimeUnit unit,long time){
        BubbleNetwork.getInstance().registerRunnable(this,r, unit,time,false,false);
    }

    public void runTaskLaterAsynchronously(Runnable r,TimeUnit unit,long time){
        BubbleNetwork.getInstance().registerRunnable(this,r, unit,time,false,true);
    }
    public void runTaskTimer(Runnable r,TimeUnit unit,long time){
        BubbleNetwork.getInstance().registerRunnable(this,r, unit,time,true,false);
    }

    public void runTaskTimerAsynchronously(Runnable r,TimeUnit unit,long time){
        BubbleNetwork.getInstance().registerRunnable(this,r, unit,time,true,true);
    }

    public void registerListener(Listener l) {
        BubbleNetwork.getInstance().registerListener(this,l);
    }

    public String getArtifact() {
        return getName();
    }

    public File getReplace() {
        return file;
    }
}
