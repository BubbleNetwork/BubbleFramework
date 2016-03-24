package com.thebubblenetwork.api.framework.cosmetics;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.manager.MainMenuManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.util.Cuboid;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//Unsafe
public class CosmeticsHook {
    private Core core;

    public CosmeticsHook(JavaPlugin plugin) {
        if (!(plugin instanceof Core)) {
            throw new IllegalArgumentException("Not UltraCosmetics");
        }
        core = (Core) plugin;
    }

    public void openMenu(Player p) {
        MainMenuManager.openMenu(p);
    }

    public void openCrate(Player p){
        //Precheck without message
        Cuboid c = new Cuboid(p.getLocation().add(-2.0D, 0.0D, -2.0D), p.getLocation().add(2.0D, 1.0D, 2.0D));
        if(c.isEmpty()) {
            TreasureChestManager.tryOpenChest(p);
        }
    }
}
