package com.thebubblenetwork.api.framework.cosmetics;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.manager.MainMenuManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

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

    public void openCrate(Player p, Location l){
        TreasureChestManager.tryOpenChest(p, l);
    }
}
