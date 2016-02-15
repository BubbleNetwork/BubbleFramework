package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import com.thebubblenetwork.api.global.player.BubblePlayerObject;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 * <p>
 * <p>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework
 * Date-created: 29/01/2016 16:56
 * Project: BubbleFramework
 */
public class BukkitBubblePlayer extends BubblePlayerObject<Player> implements BubblePlayer<Player> {
    public BukkitBubblePlayer(UUID u, PlayerData data) {
        super(u, data);
    }

    public static BukkitBubblePlayer getObject(UUID u) {
        return (BukkitBubblePlayer) getPlayerObjectMap().get(u);
    }

    public String getName(){
        return getPlayer().getName();
    }

    public void save(){
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(),new PlayerDataResponse(getName(),getData().getRaw()));
        } catch (IOException e) {
            BubbleNetwork.getInstance().logSevere("Failed to send data update: " + e.getMessage());
        }
    }
}
