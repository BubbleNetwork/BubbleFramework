package com.thebubblenetwork.api.framework.util.mc.menu;

/**
 * Created by Jacob on 12/12/2015.
 */

import com.thebubblenetwork.api.framework.BubbleNetwork;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MenuManager implements Listener {
    public static int getRoundedInventorySize(int items) {
        return items + (9 - (items % 9));
    }

    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getView().getTopInventory();
        for (Menu menu : new ArrayList<>(BubbleNetwork.getInstance().listMenu())) {
            if (menu.getInventory() == inv) {
                e.setCancelled(true);
                if (e.getClickedInventory() == inv)
                    menu.click(player, e.getClick(), e.getSlot(), e.getCurrentItem());
            }
        }
    }
}
