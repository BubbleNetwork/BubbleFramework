package com.thebubblenetwork.api.framework.plugin.manage;

import com.thebubblenetwork.api.global.plugin.Plugman;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URLClassLoader;

public class BukkitPlugman implements Plugman<JavaPlugin> {
    private Server server;
    private PluginManager manager;

    public BukkitPlugman(Server server) {
        this.server = server;
        manager = server.getPluginManager();
    }

    public void disable(JavaPlugin javaPlugin) {
        if (!javaPlugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin is already disabled");
        }
        server.getScheduler().cancelTasks(javaPlugin);
        HandlerList.unregisterAll(javaPlugin);
        manager.disablePlugin(javaPlugin);
    }

    public void enable(JavaPlugin javaPlugin) {
        if (javaPlugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin is already enabled");
        }
        manager.enablePlugin(javaPlugin);
    }

    public void unload(JavaPlugin javaPlugin) {
        if (javaPlugin.isEnabled()) {
            disable(javaPlugin);
        }
        ClassLoader cl = javaPlugin.getClass().getClassLoader();
        if (cl instanceof URLClassLoader) {
            try {
                ((URLClassLoader) cl).close();
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }

    public JavaPlugin load(File file) {
        try {
            JavaPlugin plugin = (JavaPlugin) manager.loadPlugin(file);
            plugin.onLoad();
            return plugin;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JavaPlugin get(String s) {
        return (JavaPlugin) manager.getPlugin(s);
    }
}
