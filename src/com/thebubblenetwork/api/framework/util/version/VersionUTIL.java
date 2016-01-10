package com.thebubblenetwork.api.framework.util.version;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jacob on 15/11/2015.
 */
public class VersionUTIL implements Listener {

    protected static Map<UUID, Version> versionMap = new HashMap<UUID, Version>();

    protected static Version fromPlayer(CraftPlayer player) {
        int ver = player.getHandle().playerConnection.networkManager.getVersion();
        for (Version version : Version.values())
            if (version.protocolmin <= ver && version.protocolmax >= ver)
                return version;
        return null;
    }

    public static Version getVersion(Player p) {
        return versionMap.get(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        versionMap.put(e.getPlayer().getUniqueId(), fromPlayer((CraftPlayer) e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        versionMap.remove(e.getPlayer().getUniqueId());
    }


    public enum Version {
        V17("1.7", 1.7, 4, 5), V18("1.8", 1.8, 6, 47);

        public final String string;
        public final double ver;
        public final int protocolmin, protocolmax;

        Version(String string, double ver, int protocolmin, int protocolmax) {
            this.string = string;
            this.ver = ver;
            this.protocolmin = protocolmin;
            this.protocolmax = protocolmax;
        }
    }
}
