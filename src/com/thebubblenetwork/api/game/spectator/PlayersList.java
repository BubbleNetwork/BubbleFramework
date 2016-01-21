package com.thebubblenetwork.api.game.spectator;

import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
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
 *
 *
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.game.spectator
 * Date-created: 20/01/2016 21:10
 * Project: BubbleFramework
 */
public class PlayersList extends Menu implements Listener{
    public PlayersList() {
        super(ChatColor.BLUE + "Playing", 9);
        update();
        Bukkit.getServer().getPluginManager().registerEvents(this,BubbleGameAPI.getInstance());
    }

    private Map<String,ItemStack> skulls = new HashMap<>();
    private Map<UUID,Integer> index;
    private Map<Integer,UUID> slots;

    @Override
    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if(slot < slots.size()){
            Player p = Bukkit.getPlayer(slots.get(slot));
            if(p != null){
                if(type == ClickType.LEFT){
                    player.teleport(p);
                    player.closeInventory();
                }
                else if(type == ClickType.RIGHT){
                    player.openInventory(p.getInventory());
                }
            }
        }
    }



    @Override
    public ItemStack[] generate() {
        ItemStack[] is = new ItemStack[MenuManager.getRoundedInventorySize(Bukkit.getOnlinePlayers().size() - BubbleGameAPI.getInstance().getGame().getSpectatorList().size())];
        index = new HashMap<>();
        slots = new HashMap<>();
        int i = 0;
        for(Player p:Bukkit.getOnlinePlayers()){
            if(!BubbleGameAPI.getInstance().getGame().isSpectating(p)){
                is[i] = generate(p);
                index.put(p.getUniqueId(),i);
                slots.put(i, p.getUniqueId());
                i++;
            }
        }
        if(getInventory().getSize()-9 > is.length || getInventory().getSize()+9 < is.length){
            inventory = Bukkit.createInventory(null,is.length,ChatColor.BLUE + "Playing");
        }
        return is;
    }

    public void update(Player p){
        if(!index.containsKey(p.getUniqueId())){
            update();
        }
        else getInventory().setItem(index.get(p.getUniqueId()),generate(p));
    }

    protected ItemStack generate(Player p){
        ItemStack is = generateSkull(p.getName());
        withName(is,ChatColor.AQUA + p.getName());
        withLore(is,
                ChatColor.DARK_RED + "Health: " + ChatColor.GRAY + String.valueOf(p.getHealth()) + "/" + String.valueOf(p.getMaxHealth()),
                ChatColor.YELLOW + "Hunger: " + ChatColor.GRAY + String.valueOf(p.getFoodLevel()) + "/20",
                "",
                ChatColor.DARK_PURPLE + "Left Click -> Spectate",
                ChatColor.DARK_AQUA + "Right Click -> View inventory"
        );
        return is;
    }

    private ItemStack generateSkull(String name){
        if(skulls.containsKey(name))return skulls.get(name);
        ItemStack is = new ItemStack(Material.SKULL_ITEM);
        SkullMeta meta = (SkullMeta)is.getItemMeta();
        meta.setOwner(name);
        is.setItemMeta(meta);
        skulls.put(name,is);
        return is;
    }

    private ItemStack withLore(ItemStack is,String ... lore){
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        is.setItemMeta(meta);
        return is;
    }

    private ItemStack withName(ItemStack is,String name){
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return is;
    }
}
