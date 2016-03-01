package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import com.thebubblenetwork.api.global.type.ServerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
 * Package: com.thebubblenetwork.api.game
 * Date-created: 19/01/2016 19:46
 * Project: BubbleFramework
 */
public class LobbyInventory extends BuyInventory {
    public LobbyInventory() {
        super(ChatColor.GOLD + "Teleport to lobby", "Teleport me to lobby", "Cancel");
    }

    public void onCancel(Player p) {
        p.closeInventory();
    }

    public void onAllow(Player p) {
        p.closeInventory();
        BubbleNetwork.getInstance().sendPlayer(p, ServerType.getType("Lobby"));
    }
}
