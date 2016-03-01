package com.thebubblenetwork.api.game.maps;

import com.google.common.base.Joiner;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.mc.chat.MessageUtil;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Jacob on 26/12/2015.
 */
public class VoteInventory extends Menu {
    private static final String display = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Vote";
    private static final String chance = ChatColor.BLUE + "Chance " + ChatColor.AQUA + "%chance%" + "%";
    private static final DecimalFormat format = new DecimalFormat("0.00");
    private static final Sound click = Sound.SUCCESSFUL_HIT;
    private static final ItemStackBuilder builder = new ItemStackBuilder(Material.EMPTY_MAP);

    public static VoteInventory getVoteInventory() {
        if (inventory == null) {
            inventory = new VoteInventory(GameMap.getMaps().size());
        }
        return inventory;
    }

    public static void reset() {

    }

    private static VoteInventory inventory;

    public VoteInventory(int gamemaps) {
        super(display, MenuManager.getRoundedInventorySize(gamemaps));
        update();
        BubbleNetwork.getInstance().registerMenu(BubbleGameAPI.getInstance(), this);
    }

    @Override
    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if (itemStack != null && slot < GameMap.getMaps().size()) {
            GameMap map = GameMap.getMaps().get(slot);
            if (BubbleGameAPI.getInstance().getVotes().containsKey(player.getUniqueId()) && BubbleGameAPI.getInstance().getVotes().get(player.getUniqueId()).getMap().equals(map)) {
                BubbleGameAPI.getInstance().resetVotes(player.getUniqueId());
                player.spigot().sendMessage(new MessageUtil.MessageBuilder("You cancelled your voted for ").withColor(ChatColor.BLUE).append(map.getName()).withColor(ChatColor.GRAY).withEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Joiner.on("\n" + ChatColor.GOLD).join(map.getDescription())))).build());
            } else {
                BubbleGameAPI.getInstance().addVote(player.getUniqueId(), map);
                player.spigot().sendMessage(new MessageUtil.MessageBuilder("You have voted for ").withColor(ChatColor.BLUE).append(map.getName()).withColor(ChatColor.GRAY).withEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Joiner.on("\n" + ChatColor.GOLD).join(map.getDescription())))).build());
            }
            player.playSound(player.getLocation(), click, 1f, 1f);
            update();
        }
    }

    public ItemStack[] generate() {
        ItemStack[] is = new ItemStack[getInventory().getSize()];
        Map<GameMap, Double> chancemap = BubbleGameAPI.getInstance().calculatePercentages();
        int i = 0;
        for (GameMap map : GameMap.getMaps()) {
            is[i] = builder.clone().withName(map.getName()).withLore(chance.replace("%chance%", format.format(chancemap.get(map) * 100))).withLore(map.getDescription()).build();
            i++;
        }
        return is;
    }
}
