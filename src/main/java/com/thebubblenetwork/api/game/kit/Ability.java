package com.thebubblenetwork.api.game.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

/**
 * Created by Jacob on 12/12/2015.
 */
public abstract class Ability {
    private int slot;
    private ItemStack dislay;
    private double cooldown;
    private long lastuse = 0L;

    public Ability(int slot, ItemStack dislay, double cooldown) {
        this.slot = slot;
        this.dislay = dislay;
        this.cooldown = cooldown;
    }

    public long getLastuse() {
        return lastuse;
    }

    public void setLastuse(long lastuse) {
        this.lastuse = lastuse;
    }

    public abstract void onAbilityUse(Player p, int level);

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getDislay() {
        return dislay;
    }

    public void setDislay(ItemStack dislay) {
        this.dislay = dislay;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isUsable() {
        return new Date().after(new Date(getLastuse() + ((long) (getCooldown() * 2000d))));
    }
}
