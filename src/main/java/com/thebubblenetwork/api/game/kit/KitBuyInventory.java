package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitBuyInventory extends BuyInventory {
    private static final Sound BUYKIT = Sound.LEVEL_UP, CANCELBUY = Sound.BLAZE_HIT;

    private Kit kit;

    public KitBuyInventory(Kit kit) {
        super(ChatColor.DARK_GRAY + "Buy " + ChatColor.GOLD + kit.getNameClear() + ChatColor.DARK_GRAY + " T" +
                ChatColor.GREEN + String.valueOf(kit.getPrice()));
        this.kit = kit;
    }

    public void onCancel(Player player) {
        player.closeInventory();
        KitSelection.openMenu(player);
        player.playSound(player.getLocation().getBlock().getLocation(), CANCELBUY, 1f, 1f);
    }

    public void onAllow(Player player) {
        BukkitBubblePlayer bubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId());
        if(bubblePlayer.canAfford(getKit().getPrice())) {
            bubblePlayer.setTokens(bubblePlayer.getTokens() - getKit().getPrice());
            getKit().buy(bubblePlayer);
            KitSelection.getSelection(player).update();
            player.closeInventory();
            KitSelection.openMenu(player);
            player.playSound(player.getLocation().getBlock().getLocation(), BUYKIT, 1f, 1f);
        }
        else player.sendMessage(BubbleNetwork.getPrefix() + "You can't afford this");
    }

    public Kit getKit() {
        return kit;
    }
}
