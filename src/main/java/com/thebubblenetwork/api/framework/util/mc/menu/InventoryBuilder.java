package com.thebubblenetwork.api.framework.util.mc.menu;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Jacob on 27/12/2015.
 */
public class InventoryBuilder implements Cloneable {
    private ItemStack[] stack;

    private InventoryBuilder() {

    }

    public InventoryBuilder(int size) {
        stack = new ItemStack[size];
    }

    public InventoryBuilder withSlot(ItemStack is, int slot) {
        stack[slot] = is;
        return this;
    }

    public InventoryBuilder clear() {
        stack = new ItemStack[stack.length];
        return this;
    }

    public ItemStack atSlot(int slot) {
        return stack[slot];
    }

    public ItemStack[] build() {
        return stack.clone();
    }

    public InventoryBuilder clone() {
        InventoryBuilder builder = new InventoryBuilder();
        builder.stack = build();
        return builder;
    }
}
