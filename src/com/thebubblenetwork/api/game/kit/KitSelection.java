package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import com.thebubblenetwork.api.framework.util.mc.chat.MessageUtil;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitSelection extends Menu {
    private static Sound selectkit = Sound.LEVEL_UP;
    private static MessageUtil.MessageBuilder selectkitmsg = new MessageUtil.MessageBuilder("You have selected the "
                                                                                                    + "kit ")
            .withColor(ChatColor.BLUE);
    private static String description = ChatColor.UNDERLINE + "Description";


    private static String inventoryname = ChatColor.RED + "" + ChatColor.BOLD + "Kits";
    private UUID uuid;
    private Kit kit = BubbleGameAPI.getInstance().getDefaultKit();

    public KitSelection(Player p) {
        super(inventoryname, MenuManager.getRoundedInventorySize(KitManager.getKits().size()));
        this.uuid = p.getUniqueId();
        update();
        BubbleNetwork.getInstance().getManager().addMenu(p.getUniqueId(), this);
    }

    public static String getInventoryname() {
        return inventoryname;
    }

    public static KitSelection openMenu(Player p) {
        KitSelection k = getSelection(p);
        p.openInventory(k.getInventory());
        return k;
    }

    public static void register(BubblePlugin plugin) {
        plugin.registerListener(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                BubbleNetwork.getInstance().getManager().remove(e.getPlayer().getUniqueId());
            }
        });
    }

    public static KitSelection getSelection(Player p) {
        KitSelection k = (KitSelection) BubbleNetwork.getInstance().getManager().getMenu(p.getUniqueId());
        if (k == null)
            k = new KitSelection(p);
        return k;
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


    private ItemStack[] getKits() {
        ItemStack[] is = new ItemStack[getInventory().getSize()];
        int i = 0;
        for (Kit k : KitManager.getKits()) {
            ItemStackBuilder builder = new ItemStackBuilder(k.getDisplay());
            builder.withName(k.getName());
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ");
            if (k == BubbleGameAPI.getInstance().getDefaultKit()) {
                builder.withLore(ChatColor.GRAY + "" + "      Default Kit");
            }
            String status = ChatColor.GREEN + "       Status: ";
            if (k == kit) {
                status += ChatColor.AQUA + "" + ChatColor.BOLD + "Equipped";
            }
            else if (true) {
                status += ChatColor.GRAY + "Unselected";
            }
            else {
                status += ChatColor.RED + "You have not bought this kit";
                builder.withLore(ChatColor.GREEN + "Cost: " + ChatColor.GRAY + String.valueOf(k.getPrice()));
            }
            builder.withLore(status);
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ", "");
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ");
            for (String s : k.getDescription())
                builder.withLore(ChatColor.GRAY + "" + ChatColor.ITALIC + s);
            builder.withLore(ChatColor.DARK_GRAY + "  -=========================-  ");
            ItemStack item = builder.build();
            if (k == kit)
                EnchantGlow.addGlow(item);
            is[i] = item;
            i++;
        }
        return is;
    }

    public void update() {
        getInventory().setContents(getKits());
    }

    @Override
    public void click(Player player, int slot, ItemStack itemStack) {
        if (slot < KitManager.getKits().size()) {
            Kit k = KitManager.getKits().get(slot);
            //if(hasKit){
            player.playSound(player.getLocation(), selectkit, 1f, 1f);
            this.kit = k;
            update();
            MessageUtil.MessageBuilder description = new MessageUtil.MessageBuilder(KitSelection.description);
            for (String s : k.getDescription())
                description.append(s);
            player.spigot().sendMessage(selectkitmsg.clone().append(k.getName()).withEvent(new HoverEvent(HoverEvent
                                                                                                                  .Action.SHOW_TEXT, description.build())).build());
            //}

        }
    }
}
