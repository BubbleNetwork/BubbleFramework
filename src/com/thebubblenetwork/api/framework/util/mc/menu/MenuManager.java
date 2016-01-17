package com.thebubblenetwork.api.framework.util.mc.menu;

/**
 * Created by Jacob on 12/12/2015.
 */

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener {

    private Map<Object, Menu> menus = new HashMap<Object, Menu>();

    public MenuManager() {

    }

    public static int getRoundedInventorySize(int items) {
        return items + (9 - (items % 9));
    }

    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void clear() {
        menus.clear();
    }

    public void addMenu(Object key, Menu menu) {
        menus.put(key, menu);
    }

    public boolean isMenu(Object key) {
        return menus.containsKey(key);
    }

    public void remove(Object key) {
        menus.remove(key);
    }

    public Collection<Menu> getMenus() {
        return menus.values();
    }

    public Menu getMenu(Object key) {
        return menus.get(key);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getView().getTopInventory();
        for (Menu menu : menus.values()) {
            if (menu.getInventory().equals(inv)) {
                e.setCancelled(true);
                if (e.getClickedInventory().equals(inv))
                    menu.click(player,e.getClick(),e.getSlot(), e.getCurrentItem());
            }
        }
    }
}
