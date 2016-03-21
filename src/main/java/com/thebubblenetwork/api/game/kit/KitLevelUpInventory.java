package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
 * Package: com.thebubblenetwork.api.game.kit
 * Date-created: 17/01/2016 13:43
 * Project: BubbleFramework
 */

public class KitLevelUpInventory extends BuyInventory {
    private static final Sound BUYKIT = Sound.LEVEL_UP, CANCELBUY = Sound.BLAZE_HIT;
    private int cost;
    private Kit k;
    private boolean cancelled = false;
    private int level;

    public KitLevelUpInventory(Kit k, int cost, int level) {
        super(ChatColor.GOLD + k.getNameClear() + " Lv" + String.valueOf(level - 1) + " -> Lv" + String.valueOf(level) + " " + ChatColor.RED + String.valueOf(k.getPrice()) + "T");
        this.k = k;
        this.cost = cost;
        this.level = level;
        BubbleGameAPI.getInstance().registerListener(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent e) {
                if (e.getInventory() == getInventory()) {
                    cancelled = true;
                    BubbleNetwork.getInstance().unregisterMenu(KitLevelUpInventory.this);
                    HandlerList.unregisterAll(this);
                }
            }
        });
    }

    public Kit getKit() {
        return k;
    }

    public int getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void onCancel(Player player) {
        player.closeInventory();
        KitSelection.openMenu(player);
        player.playSound(player.getLocation().getBlock().getLocation(), CANCELBUY, 1f, 1f);
    }

    public void onAllow(Player player) {
        BukkitBubblePlayer bubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId());
        getKit().level(bubblePlayer, getLevel());
        KitSelection.getSelection(player).update();
        player.closeInventory();
        KitSelection.openMenu(player);
        player.playSound(player.getLocation().getBlock().getLocation(), BUYKIT, 1f, 1f);
    }
}
