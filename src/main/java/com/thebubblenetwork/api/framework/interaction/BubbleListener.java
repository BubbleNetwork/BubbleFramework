package com.thebubblenetwork.api.framework.interaction;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.PlayerCountUpdate;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerDataRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import com.thebubblenetwork.api.global.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class BubbleListener implements Listener{
    private BubbleNetwork network;

    private Map<String,Map<String,String>> data = new HashMap<>();

    protected BubbleNetwork getNetwork(){
        return network;
    }

    public Map<String,Map<String,String>> getData(){
        return data;
    }

    public BubbleListener(BubbleNetwork network) {
        this.network = network;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreJoin(AsyncPlayerPreLoginEvent e){
        data.put(e.getName(),DataRequestTask.requestAsync(e.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitPlayecount(PlayerQuitEvent e){
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(),new PlayerCountUpdate(Bukkit.getOnlinePlayers().size()-1));
        } catch (IOException e1) {
            getNetwork().logSevere(e1.getMessage());
            getNetwork().logSevere("Could not send playercount update");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitRemoval(PlayerQuitEvent e){
        BukkitBubblePlayer.getPlayerObjectMap().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        BukkitBubblePlayer.getPlayerObjectMap().put(p.getUniqueId(),new BukkitBubblePlayer(p.getUniqueId(),new PlayerData(data.remove(p.getName()))));
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(),new PlayerCountUpdate(Bukkit.getOnlinePlayers().size()));
        } catch (IOException e1) {
            getNetwork().logSevere(e1.getMessage());
            getNetwork().logSevere("Could not send playercount update");
        }
    }


}
