package com.thebubblenetwork.api.framework.util.version;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jacob on 15/11/2015.
 */
public class VersionUTIL implements Listener {
    private static Class<?> craftplayer,entityplayer,playerconnection,networkmanager;
    private static Method gethandle,getversion;
    private static Field playerconnectionfield,networkmanagerfield;

    static{
        try{
            craftplayer = ReflectionUTIL.getCraftClass("entity.CraftPlayer");
            entityplayer = ReflectionUTIL.getNMSClass("EntityPlayer");
            playerconnection = ReflectionUTIL.getNMSClass("PlayerConnection");
            networkmanager = ReflectionUTIL.getNMSClass("NetworkManager");
            gethandle = ReflectionUTIL.getMethod(craftplayer,"getHandle",true);
            getversion = ReflectionUTIL.getMethod(networkmanager,"getVersion",true);
            playerconnectionfield = ReflectionUTIL.getField(entityplayer,"playerConnection",true);
            networkmanagerfield = ReflectionUTIL.getField(playerconnection,"networkManager",true);
        }
        catch (Exception ex){
            Logger.getGlobal().log(Level.WARNING,"Could not setup Version Util",ex);
        }
    }

    private static Object getVersionObject(Player p) throws Exception{
        if(craftplayer.isInstance(p)){
            Object entityplayer = ReflectionUTIL.invoke(gethandle,p);
            if(VersionUTIL.entityplayer.isInstance(entityplayer)){
                Object playerconnection = playerconnectionfield.get(entityplayer);
                if(VersionUTIL.playerconnection.isInstance(playerconnection)){
                    Object networkManager = networkmanagerfield.get(playerconnection);
                    if(networkmanager.isInstance(networkManager)){
                        return ReflectionUTIL.invoke(getversion,networkManager);
                    }
                    throw new Exception("NetworkManager instance not found");
                }
                throw new Exception("PlayerConnection instance not found");
            }
            throw new Exception("EntityPlayer instance not found");
        }
        throw new Exception("CraftPlayer instance not found");
    }

    private static int getVersionInt(Player p){
        try{
            return (Integer)getVersionObject(p);
        }
        catch (Exception ex){
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING,"Could get version of " + p.getName(),ex);
            return Version.V18.protocolmin;
        }
    }

    protected static Map<UUID, Version> versionMap = new HashMap<UUID, Version>();

    protected static Version fromPlayer(Player p) {
        int ver = getVersionInt(p);
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
        versionMap.put(e.getPlayer().getUniqueId(), fromPlayer(e.getPlayer()));
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
