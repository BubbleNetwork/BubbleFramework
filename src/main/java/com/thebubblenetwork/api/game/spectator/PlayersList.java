package com.thebubblenetwork.api.game.spectator;

import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
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

import java.text.DecimalFormat;
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
 * <p/>
 * <p/>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.game.spectator
 * Date-created: 20/01/2016 21:10
 * Project: BubbleFramework
 */
public class PlayersList extends Menu implements Listener {
    private static DecimalFormat health = new DecimalFormat("#.#");

    private Map<String, ItemStack> skulls = new HashMap<>();
    private Map<UUID, Integer> index = new HashMap<>();
    private Map<Integer, UUID> slots = new HashMap<>();

    public PlayersList() {
        super(ChatColor.BLUE + "Playing", 9);
        update();
        BubbleGameAPI.getInstance().registerListener(this);
    }

    @Override
    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if (slots.containsKey(slot)) {
            Player p = Bukkit.getPlayer(slots.get(slot));
            if (p.isOnline() && !p.isDead() && (type == ClickType.LEFT || type == ClickType.SHIFT_LEFT)) {
                player.teleport(p);
                player.closeInventory();
            } else if (type == ClickType.RIGHT || type == ClickType.SHIFT_RIGHT) {
                player.openInventory(p.getInventory());
            }
        }
    }


    @Override
    public ItemStack[] generate() {
        ItemStack[] is = new ItemStack[getRoundedInventorySize(Bukkit.getOnlinePlayers().size() - BubbleGameAPI.getInstance().getGame().getSpectatorList().size())];
        index.clear();
        slots.clear();
        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!BubbleGameAPI.getInstance().getGame().isSpectating(p)) {
                is[i] = generate(p);
                index.put(p.getUniqueId(), i);
                slots.put(i, p.getUniqueId());
                i++;
            }
        }
        if (getInventory().getSize() != is.length) {
            inventory = Bukkit.createInventory(this, is.length == 0 ? 9: is.length, ChatColor.BLUE + "Playing");
        }
        return is;
    }

    public void update(Player p) {
        if (!index.containsKey(p.getUniqueId())) {
            update();
        } else {
            getInventory().setItem(index.get(p.getUniqueId()), generate(p));
        }
    }

    protected ItemStack generate(Player p) {
        ItemStack is = generateSkull(p.getName());
        withName(is, ChatColor.AQUA + p.getName());
        withLore(is, ChatColor.DARK_RED + "Health: " + ChatColor.GRAY + health.format(p.getHealth()) + "/" + String.valueOf((int)p.getMaxHealth()), ChatColor.YELLOW + "Hunger: " + ChatColor.GRAY + String.valueOf(p.getFoodLevel()) + "/20", "", ChatColor.DARK_PURPLE + "Left Click -> Spectate", ChatColor.DARK_AQUA + "Right Click -> View inventory");
        return is;
    }

    private ItemStack generateSkull(String name) {
        if (skulls.containsKey(name)) {
            return skulls.get(name).clone();
        }
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwner(name);
        is.setItemMeta(meta);
        skulls.put(name, is);
        return is.clone();
    }

    private ItemStack withLore(ItemStack is, String... lore) {
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        is.setItemMeta(meta);
        return is;
    }

    private ItemStack withName(ItemStack is, String name) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return is;
    }
}
