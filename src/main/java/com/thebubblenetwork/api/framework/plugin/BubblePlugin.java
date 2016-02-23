package com.thebubblenetwork.api.framework.plugin;

import com.avaje.ebean.EbeanServer;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.P;
import com.thebubblenetwork.api.global.plugin.updater.FileUpdater;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Jacob on 09/12/2015.
 */
public abstract class BubblePlugin implements Plugin,FileUpdater {
    private PluginDescriptionFile descriptionFile;
    private File file;

    public BubblePlugin(){
        BubbleNetwork.getInstance().addUpdater(this);
    }

    public org.bukkit.plugin.PluginDescriptionFile getDescription() {
        return PluginDescriptionFile.asMirror(descriptionFile);
    }

    public PluginDescriptionFile getDescriptionBubble() {
        return descriptionFile;
    }

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    public InputStream getResource(String s) {
        return getPlugin().getResource(s);
    }

    @Deprecated
    public void saveConfig() {
    }

    @Deprecated
    public void saveDefaultConfig() {
    }

    @Deprecated
    public void saveResource(String s, boolean b) {
    }

    @Deprecated
    public void reloadConfig() {

    }

    public PluginLoader getPluginLoader() {
        return getPlugin().getPluginLoader();
    }

    public boolean isEnabled() {
        return getPlugin().isEnabled();
    }

    public void __init__(BubblePluginLoader loader) {
        file = loader.getJar();
        descriptionFile = loader.getFile();
    }

    public void onLoad() {
    }

    public boolean isNaggable() {
        return false;
    }

    @Deprecated
    public void setNaggable(boolean b) {
    }

    public EbeanServer getDatabase() {
        return getPlugin().getDatabase();
    }

    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return getPlugin().getDefaultWorldGenerator(s, s1);
    }

    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Deprecated
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
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
