package com.thebubblenetwork.api.framework.util.mc.items;

import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnchantGlow extends EnchantmentWrapper {
    public static void kill() {
        glow = null;
    }

    public static Enchantment getGlow() {
        if (glow == null) {
            glow = Enchantment.getByName("Glow");
            if (glow == null) {
                glow = new EnchantGlow(255);
                ReflectionUTIL.set(acceptingNew, null, true);
                Enchantment.registerEnchantment(glow);
            }
        }
        return glow;
    }

    public static void addGlow(ItemStack var0) {
        Enchantment var1 = getGlow();
        var0.addEnchantment(var1, 1);
    }

    private static Enchantment glow;
    private static Field acceptingNew;

    static {
        try {
            acceptingNew = ReflectionUTIL.getField(Enchantment.class, "acceptingNew", true);
        } catch (NoSuchFieldException e) {
            Logger.getGlobal().log(Level.WARNING, "Could not setup EnchantGlow", e);
        }
    }

    public EnchantGlow(int var1) {
        super(var1);
    }

    public boolean canEnchantItem(ItemStack var1) {
        return true;
    }

    public boolean conflictsWith(Enchantment var1) {
        return false;
    }

    public EnchantmentTarget getItemTarget() {
        return null;
    }

    public int getMaxLevel() {
        return 10;
    }

    public String getName() {
        return "Glow";
    }

    public int getStartLevel() {
        return 1;
    }
}
