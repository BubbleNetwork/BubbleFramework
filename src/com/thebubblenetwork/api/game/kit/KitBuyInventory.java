package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BubblePlayer;
import com.thebubblenetwork.api.framework.data.PlayerData;
import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitBuyInventory extends BuyInventory {
    public KitBuyInventory(Kit kit) {
        super(ChatColor.DARK_GRAY + "Buy " + ChatColor.GOLD + kit.getNameClear() + ChatColor.DARK_GRAY + " for " +
                      ChatColor.GREEN + String.valueOf(kit.getPrice()), "kit_" + kit.getNameClear());
    }

    public static KitBuyInventory getViaMap(String name) {
        return (KitBuyInventory) BubbleNetwork.getInstance().getManager().getMenu("kit_" + name);
    }

    @Override
    public void onCancel(Player player) {
        player.closeInventory();
        KitSelection.openMenu(player);
    }

    @Override
    public void onAllow(Player player) {
        player.closeInventory();
        //TODO - Kit buyingKitSelection.openMenu(player);
    }
}
