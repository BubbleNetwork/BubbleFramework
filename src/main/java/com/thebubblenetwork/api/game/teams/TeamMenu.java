package com.thebubblenetwork.api.game.teams;

import com.google.common.base.Joiner;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.mc.chat.MessageUtil;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class TeamMenu extends Menu {

    private static final String display = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Team Selection";
    private static final Sound click = Sound.SUCCESSFUL_HIT;
    private static final ItemStackBuilder builder = new ItemStackBuilder(Material.FIREBALL);
    private static final Map<UUID, TeamMenu> cached = new HashMap<>();
    private static ItemStack[] contents;

    public static TeamMenu getMenu(Player p) {
        TeamMenu menu = cached.containsKey(p.getUniqueId()) ? cached.get(p.getUniqueId()) : new TeamMenu();
        cached.put(p.getUniqueId(), menu);
        return menu;
    }

    public static void removeMenu(Player p){
        if(cached.containsKey(p.getUniqueId())) {
            cached.remove(p.getUniqueId()).deregister();
        }
    }

    public static ItemStack[] generateInventory() {
        //If it has been reset we can add

        ItemStack[] is = new ItemStack[9];

        ItemStackBuilder redTeam = new ItemStackBuilder(Material.WOOL).withColor(DyeColor.RED).withName(ChatColor.RED + "Red Team");
        ItemStackBuilder blueTeam = new ItemStackBuilder(Material.WOOL).withColor(DyeColor.BLUE).withName(ChatColor.BLUE + "Blue Team");

        is[0] = redTeam.build();
        is[1] = blueTeam.build();

        return is;
    }

    public static void updateAll(){
        contents = generateInventory();
        for(TeamMenu menu: cached.values()){
            menu.update();
        }
    }

    private TeamMenu() {
        super(display, 9);
        BubbleNetwork.getInstance().registerMenu(BubbleGameAPI.getInstance(), this);
        update();
    }

    public void deregister(){
        BubbleNetwork.getInstance().unregisterMenu(this);
    }

    private UUID uuid = null;

    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if (slot == 0 && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase("Red Team")) {
            BubbleGameAPI.getInstance().getTeamManager().addToTeam(TeamType.RED, player);
        } else if (slot == 1 && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase("Blue Team")) {
            BubbleGameAPI.getInstance().getTeamManager().addToTeam(TeamType.BLUE, player);
        }
        player.playSound(player.getLocation(), click, 1f, 1f);
        uuid = player.getUniqueId();
        updateAll();
    }

    @Override
    public ItemStack[] generate() {
        if(contents == null) {
            contents = generateInventory();
        }
        ItemStack[] generate = contents.clone();

        return generate;
    }


}
