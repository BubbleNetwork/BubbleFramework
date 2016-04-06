package com.thebubblenetwork.api.framework.player;

import com.thebubblenetwork.api.framework.event.PlayerDataReceivedEvent;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.UUID;
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
 * Date-created: 29/01/2016 16:56
 * Project: BubbleFramework
 */
public class BukkitBubblePlayer extends BubblePlayer<Player> {
    public static BukkitBubblePlayer getObject(UUID u) {
        return (BukkitBubblePlayer) getPlayerObjectMap().get(u);
    }

    public BukkitBubblePlayer(UUID u, PlayerData data) {
        super(u, data.getRaw());
    }

    public String getName() {
        return getPlayer().getName();
    }

    public boolean isOnline(){
        return getPlayer() != null && getPlayer().isOnline();
    }

    public void save() {
        throw new UnsupportedOperationException("Server-side cannot save");
    }

    protected void update() {
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(), new PlayerDataResponse(getName(), getData().getRaw()));
            new BukkitRunnable(){
                public void run() {
                    //Doesn't change before & after but nothing can be done
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerDataReceivedEvent(getPlayer(), getData()));
                }
            }.runTaskAsynchronously(BubbleNetwork.getInstance().getPlugin());
        } catch (IOException e) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Failed to send data update: ", e);
        }
    }
}
