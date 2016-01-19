package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 *
 *
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.game
 * Date-created: 19/01/2016 19:46
 * Project: BubbleFramework
 */
public class HubInventory extends BuyInventory{
    public HubInventory() {
        super(ChatColor.GOLD + "Teleport to hub", "hub_teleport", "Teleport me to hub", "Cancel");
    }

    @Override
    public void onCancel(Player p) {
        p.closeInventory();
    }

    @Override
    public void onAllow(Player p) {
        p.closeInventory();
        p.sendMessage("WIP");
    }
}
