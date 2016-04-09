package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import com.thebubblenetwork.api.framework.util.mc.chat.MessageUtil;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitSelection extends Menu {
    public static String getInventoryname() {
        return inventoryname;
    }

    public static KitSelection openMenu(Player p) {
        KitSelection k = getSelection(p);
        k.show(p);
        return k;
    }

    public static void register(BubbleAddon plugin) {
        plugin.registerListener(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                try {
                    BubbleNetwork.getInstance().unregisterMenu(menuMap.remove(e.getPlayer().getUniqueId()));
                }
                catch (Exception ex){
                }
            }
        });
    }

    public static Map<UUID, KitSelection> getMenuMap() {
        return menuMap;
    }

    public static KitSelection getSelection(Player p) {
        if(!menuMap.containsKey(p.getUniqueId())){
            KitSelection selection = new KitSelection(p);
            menuMap.put(p.getUniqueId(), selection);
            return selection;
        }
        return menuMap.get(p.getUniqueId());
    }

    private static Sound selectkit = Sound.LEVEL_UP, buykit = Sound.NOTE_BASS, noaccess = Sound.BLAZE_DEATH;
    private static MessageUtil.MessageBuilder selectkitmsg = new MessageUtil.MessageBuilder("You have selected ").color(ChatColor.BLUE);
    private static MessageUtil.MessageBuilder noaccessmsg = new MessageUtil.MessageBuilder("No do not have ").color(ChatColor.RED);
    private static String inventoryname = ChatColor.RED + "" + ChatColor.BOLD + "Kits";
    private static Map<UUID, KitSelection> menuMap = new HashMap<>();
    private UUID uuid;
    private BukkitBubblePlayer player;
    private Kit kit = BubbleGameAPI.getInstance().getDefaultKit();

    public KitSelection(Player p) {
        super(inventoryname, getRoundedInventorySize(KitManager.getKits().size()));
        this.uuid = p.getUniqueId();
        this.player = BukkitBubblePlayer.getObject(p.getUniqueId());
        BubbleNetwork.getInstance().registerMenu(BubbleGameAPI.getInstance(), this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }


    public ItemStack[] generate() {
        ItemStack[] is = new ItemStack[getInventory().getSize()];
        int i = 0;
        for (Kit k : KitManager.getKits()) {
            ItemStackBuilder builder = new ItemStackBuilder(k.getDisplay());
            builder.withName(k.getName());
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ");
            if (k == BubbleGameAPI.getInstance().getDefaultKit()) {
                builder.withLore(ChatColor.GRAY + "" + "      Default Kit");
            }
            String rightclick = ChatColor.DARK_AQUA + "Right Click -> ";
            String leftclick = ChatColor.DARK_PURPLE + "Left Click -> ";
            String status = ChatColor.GREEN + "       Status: ";
            if (k == kit) {
                status += ChatColor.AQUA + "" + ChatColor.BOLD + "Equipped";
                int level = k.getLevel(player);
                builder.withLore(ChatColor.GREEN + "       Level: " +
                        (level == k.getMaxlevel() ? ChatColor.GOLD.toString() + ChatColor.BOLD : level == 0 ? ChatColor.RED : ChatColor.GRAY) + String.valueOf(level));
                leftclick = null;
                if (k.getLevel(player) < k.getMaxlevel()) {
                    rightclick += "Upgrade this kit";
                } else {
                    rightclick = ChatColor.GOLD + "You have mastered this kit";
                }
            } else if (k.isOwned(player)) {
                status += ChatColor.GRAY + "Unselected";
                int level = k.getLevel(player);
                builder.withLore(ChatColor.GREEN + "       Level: " +
                        (level == k.getMaxlevel() ? ChatColor.GOLD.toString() + ChatColor.BOLD : level == 0 ? ChatColor.RED : ChatColor.GRAY) + String.valueOf(level));
                leftclick += "Select this kit";
                if (k.getLevel(player) < k.getMaxlevel()) {
                    rightclick += "Upgrade this kit";
                } else {
                    rightclick = ChatColor.GOLD + "You have mastered this kit";
                }
            } else {
                status += ChatColor.RED + "You have not bought this kit";
                builder.withLore(ChatColor.GREEN + "       Cost: " + ChatColor.GRAY + String.valueOf(k.getPrice()));
                leftclick = null;
                rightclick += "Buy this kit";
            }
            builder.withLore(status);
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ", "");
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ");
            for (String s : k.getDescription()) {
                builder.withLore(ChatColor.GRAY + "" + ChatColor.ITALIC + s);
            }
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ", "");
            if (leftclick != null) {
                builder.withLore(leftclick);
            }
            builder.withLore(rightclick);
            ItemStack item = builder.build();
            if (k == kit) {
                EnchantGlow.addGlow(item);
            }
            is[i] = item;
            i++;
        }
        return is;
    }

    @Override
    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if (slot < KitManager.getKits().size()) {
            Kit k = KitManager.getKits().get(slot);
            BukkitBubblePlayer bubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId());
            if (type == ClickType.LEFT) {
                ComponentBuilder description = new ComponentBuilder(k.getName()).append("\n").append("\n");
                for (String s : k.getDescription()) {
                    description.append(s).color(ChatColor.GRAY).italic(true).append("\n");
                }
                if (k.isOwned(bubblePlayer)) {
                    player.playSound(player.getLocation().getBlock().getLocation(), selectkit, 1f, 1f);
                    this.kit = k;
                    player.spigot().sendMessage(selectkitmsg.clone().append(k.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, description.create())).create());
                    update();
                } else {
                    player.playSound(player.getLocation().getBlock().getLocation(), noaccess, 1f, 1f);
                    player.spigot().sendMessage(noaccessmsg.clone().append(k.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, description.create())).create());
                }
            } else if (type == ClickType.RIGHT) {
                player.playSound(player.getLocation().getBlock().getLocation(), buykit, 1f, 1f);
                if (k.isOwned(bubblePlayer)) {
                    if(k.getLevel(bubblePlayer) < k.getMaxlevel()) {
                        if (bubblePlayer.canAfford(k.getLevelUpcost(bubblePlayer))) {
                            KitLevelUpInventory kitLevelUpInventory = new KitLevelUpInventory(k, k.getLevelUpcost(bubblePlayer), k.getLevel(bubblePlayer) + 1);
                            kitLevelUpInventory.show(player);
                        } else player.sendMessage(BubbleNetwork.getPrefix() + "You can't afford this");
                    }
                    else{
                        player.sendMessage(BubbleNetwork.getPrefix() + "You have mastered this kit");
                    }
                } else {
                    if(bubblePlayer.canAfford(kit.getPrice())) {
                        k.getBuyInventory().show(player);
                    }
                    else player.sendMessage(BubbleNetwork.getPrefix() + "You can't afford this");
                }
            }
        }
    }

    @Override
    public void show(Player p) {
        update();
        p.openInventory(getInventory());
    }
}
