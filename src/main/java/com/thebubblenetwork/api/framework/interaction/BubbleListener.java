package com.thebubblenetwork.api.framework.interaction;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.PlayerCountUpdate;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 * <p/>
 * <p/>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework
 * Date-created: 30/01/2016 00:12
 * Project: BubbleFramework
 */
public class BubbleListener implements Listener {
    private BubbleNetwork network;

    private Map<String, Map<String, String>> data = new HashMap<>();

    public BubbleListener(BubbleNetwork network) {
        this.network = network;
    }

    protected BubbleNetwork getNetwork() {
        return network;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    @EventHandler
    public void onRainSnow(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        data.put(e.getName(), DataRequestTask.requestAsync(e.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitPlayecount(PlayerQuitEvent e) {
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new PlayerCountUpdate(Bukkit.getOnlinePlayers().size() - 1));
        } catch (IOException e1) {
            getNetwork().getLogger().log(Level.WARNING, "Could not send playercount update", e1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitRemoval(PlayerQuitEvent e) {
        BukkitBubblePlayer.getPlayerObjectMap().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int i = 0;
        while (!data.containsKey(p.getName())) {
            if (i == 4000) {
                e.setJoinMessage(null);
                e.getPlayer().kickPlayer(ChatColor.RED + "Server timed out");
                return;
            }
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e1) {
            }
            i++;
        }
        BukkitBubblePlayer player = new BukkitBubblePlayer(p.getUniqueId(), new PlayerData(data.remove(p.getName())));
        BukkitBubblePlayer.getPlayerObjectMap().put(p.getUniqueId(), player);
        //Sets up new permission attachment
        final PermissionAttachment attachment = p.addAttachment(getNetwork().getPlugin());
        //Doing async to prevent lag
        final Rank playerRank = player.getRank();
        new BukkitRunnable() {
            public void run() {
                Rank r = playerRank;
                boolean b;
                //Loop through all the rank permissions, top to bottom
                while(r != null){
                    for(Map.Entry<String,String> entry:r.getData().getRaw().entrySet()){
                        String permission = entry.getKey();
                        try{
                            b = Boolean.parseBoolean(entry.getValue());
                        }
                        catch (Exception ex){
                            //If parse fails
                            continue;
                        }
                        //Skip if permission has been set in higher inheritance
                        if(!attachment.getPermissible().isPermissionSet(permission)){
                            //Set permission
                            attachment.setPermission(permission,b);
                        }
                    }
                    r = r.getInheritance();
                }
            }
        }.runTaskAsynchronously(BubbleNetwork.getInstance().getPlugin());
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new PlayerCountUpdate(Bukkit.getOnlinePlayers().size()));
        } catch (IOException e1) {
            getNetwork().getLogger().log(Level.INFO, "Could not send playercount update", e1);
        }

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }


}
