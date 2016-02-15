package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import com.thebubblenetwork.api.framework.util.mc.menu.BuyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitBuyInventory extends BuyInventory {
    private static final Sound
            BUYKIT = Sound.LEVEL_UP,
            CANCELBUY = Sound.BLAZE_HIT;

    private Kit kit;

    public KitBuyInventory(Kit kit) {
        super(ChatColor.DARK_GRAY + "Buy " + ChatColor.GOLD + kit.getNameClear() + ChatColor.DARK_GRAY + " T" +
                ChatColor.GREEN + String.valueOf(kit.getPrice()), "kit_buy_" + kit.getNameClear());
        this.kit = kit;
    }

    public static KitBuyInventory getViaMap(String name) {
        return (KitBuyInventory) BubbleNetwork.getInstance().getManager().getMenu("kit_buy_" + ChatColorAppend.wipe(name));
    }

    public void onCancel(Player player) {
        KitSelection.openMenu(player);
        player.playSound(player.getLocation().getBlock().getLocation(), CANCELBUY, 1f, 1f);
    }

    public void onAllow(Player player) {
        BukkitBubblePlayer bubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId());
        getKit().buy(bubblePlayer);
        KitSelection.openMenu(player);
        player.playSound(player.getLocation().getBlock().getLocation(), BUYKIT, 1f, 1f);
    }

    public Kit getKit() {
        return kit;
    }
}
