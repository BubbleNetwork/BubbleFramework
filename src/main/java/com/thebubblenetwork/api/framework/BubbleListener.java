package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.interaction.DataRequestTask;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.PlayerCountUpdate;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
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

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if(inv != null && e.getClickedInventory() != null) {
            for (Menu menu : BubbleNetwork.getInstance().listMenu()) {
                if (menu.getInventory().equals(inv)) {
                    e.setCancelled(true);
                    if (e.getClickedInventory().equals(inv)) {
                        menu.click(player, e.getClick(), e.getSlot(), e.getCurrentItem());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int i = 0;
        if(!data.containsKey(p.getName())){
            p.kickPlayer("Server timed out");
            throw new IllegalArgumentException("Player not found");
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        final BukkitBubblePlayer player = new BukkitBubblePlayer(p.getUniqueId(), new PlayerData(data.remove(p.getName())));
        BukkitBubblePlayer.getPlayerObjectMap().put(p.getUniqueId(), player);
        //Sets up new permission attachment
        final PermissionAttachment attachment = p.addAttachment(getNetwork().getPlugin());
        new Thread(){
            @Override
            public void run(){
                Rank r = player.getRank();
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
                        if(!attachment.getPermissions().containsKey(permission)){
                            //Set permission
                            attachment.setPermission(permission,b);
                        }
                    }
                    r = r.getInheritance();
                }
                try {
                    getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new PlayerCountUpdate(Bukkit.getOnlinePlayers().size()));
                } catch (IOException e1) {
                    getNetwork().getLogger().log(Level.INFO, "Could not send playercount update", e1);
                }
            }
        }.start();
    }

}
