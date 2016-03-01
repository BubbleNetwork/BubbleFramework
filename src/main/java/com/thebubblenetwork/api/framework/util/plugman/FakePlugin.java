package com.thebubblenetwork.api.framework.util.plugman;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FakePlugin implements Plugin {
    @Deprecated
    public File getDataFolder() {
        return null;
    }

    @Deprecated
    public PluginDescriptionFile getDescription() {
        return new PluginDescriptionFile("FakePlugin", "0", getClass().getName());
    }

    @Deprecated
    public FileConfiguration getConfig() {
        return null;
    }

    @Deprecated
    public InputStream getResource(String s) {
        return null;
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

    @Deprecated
    public PluginLoader getPluginLoader() {
        return null;
    }

    @Deprecated
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Deprecated
    public boolean isEnabled() {
        return true;
    }

    @Deprecated
    public void onDisable() {

    }

    @Deprecated
    public void onLoad() {

    }

    @Deprecated
    public void onEnable() {

    }

    @Deprecated
    public boolean isNaggable() {
        return false;
    }

    @Deprecated
    public void setNaggable(boolean b) {

    }

    @Deprecated
    public EbeanServer getDatabase() {
        return null;
    }

    @Deprecated
    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return null;
    }

    @Deprecated
    public Logger getLogger() {
        return Logger.getLogger("Minecraft");
    }

    @Deprecated
    public String getName() {
        return "FakePlugin";
    }

    @Deprecated
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Deprecated
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
