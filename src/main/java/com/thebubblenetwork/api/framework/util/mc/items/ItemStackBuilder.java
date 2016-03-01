package com.thebubblenetwork.api.framework.util.mc.items;

import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacob on 12/12/2015.
 */
public class ItemStackBuilder implements Cloneable {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(Material mat) {
        this.ITEM_STACK = new ItemStack(mat);
    }

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemStackBuilder() {
        this.ITEM_STACK = new ItemStack(Material.AIR);
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public String getName() {
        return ITEM_STACK.getItemMeta().getDisplayName();
    }

    public ItemStackBuilder withGlow() {
        wipeEnchants();
        withEnchantment(EnchantGlow.getGlow());
        return this;
    }


    public ItemStackBuilder wipeEnchants() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setDisplayName(ChatColorAppend.translate(name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ChatColorAppend.translate(name));
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(String... name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        for (String s : name) {
            lore.add(ChatColorAppend.translate(s));
        }
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(Iterable<String> name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        for (String s : name) {
            lore.add(ChatColorAppend.translate(s));
        }
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withDurability(int durability) {
        ITEM_STACK.setDurability((short) durability);
        return this;
    }

    public ItemStackBuilder withData(int data) {
        ITEM_STACK.setDurability((short) data);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantmentIntegerMap) {
        ITEM_STACK.addUnsafeEnchantments(enchantmentIntegerMap);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder clearLore() {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setLore(new ArrayList<String>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(DyeColor color) {
        ITEM_STACK.setDurability(color.getWoolData());
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStackBuilder withUnbreaking(boolean unbreaking) {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.spigot().setUnbreakable(unbreaking);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return ITEM_STACK.clone();
    }

    @Override
    public ItemStackBuilder clone() {
        return new ItemStackBuilder(build());
    }
}
