package com.thebubblenetwork.api.game.maps;

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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;

/**
 * The Bubble Network 2016
 * BubbleFramework
 * 07/04/2016 {08:47}
 * Created April 2016
 */
public class VoteMenu extends Menu{
    private static final String display = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Vote";
    private static final String chance = ChatColor.BLUE + "Chance " + ChatColor.AQUA + "%chance%" + "%";
    private static final DecimalFormat format = new DecimalFormat("0.00");
    private static final Sound click = Sound.SUCCESSFUL_HIT;
    private static final ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER);
    private static final List<GameMap> mapList = new ArrayList<>();
    private static final Map<GameMap, Integer> slotMap = new HashMap<>();
    private static ItemStack[] contents;
    private static final Map<UUID, GameMap> votes = new HashMap<>();
    private static HashMap<UUID, VoteMenu> cached = new HashMap<>();

    public static VoteMenu getMenu(Player p){
        VoteMenu menu = cached.containsKey(p.getUniqueId()) ? cached.get(p.getUniqueId()) : new VoteMenu();
        cached.putIfAbsent(p.getUniqueId(), menu);
        return menu;
    }

    public static Collection<GameMap> getVotes(){
        return votes.values();
    }

    public static void removeMenu(Player p){
        if(cached.containsKey(p.getUniqueId())) {
            cached.remove(p.getUniqueId()).deregister();
        }
    }

    public static int getAmountOfVotes(){
        return votes.size();
    }

    public static void wipeClean(){
        contents = null;
        for(VoteMenu menu:cached.values()){
            menu.deregister();
        }
        votes.clear();
        mapList.clear();
        cached.clear();
        slotMap.clear();
    }

    public static ItemStack[] generateInventory() {
        //If it has been reset we can add
        if(mapList.isEmpty()){
            mapList.addAll(GameMap.getMaps());
            Collections.shuffle(mapList);
        }
        Map<GameMap, Double> chancemap = BubbleGameAPI.getInstance().calculatePercentages();

        //Sort the maps into correct order based on votes
        Collections.sort(mapList, new Comparator<GameMap>() {
            public int compare(GameMap o1, GameMap o2) {
                return (int)((chancemap.get(o2) - chancemap.get(o1))*1000);
            }
        });

        ItemStack[] is = new ItemStack[Menu.getRoundedInventorySize(mapList.size())];

        //Clear all the slots
        slotMap.clear();
        int i = 0;
        for (GameMap map : mapList) {
            ItemStackBuilder builder = VoteMenu.builder.clone().withName(map.getName()).withLore("").withLore(chance.replace("%chance%", format.format(chancemap.get(map) * 100))).withLore("");
            for(String s: map.getDescription()){
                builder.withLore(ChatColor.GRAY + ChatColor.ITALIC.toString() + s);
            }
            is[i] = builder.build();
            slotMap.put(map, i);
            i++;
        }
        return is;
    }

    public static void updateAll(){
        contents = generateInventory();
        for(VoteMenu menu: cached.values()){
            menu.update();
        }
    }

    private VoteMenu() {
        super(display, Menu.getRoundedInventorySize(GameMap.getMaps().size()));
        BubbleNetwork.getInstance().registerMenu(BubbleGameAPI.getInstance(), this);
        update();
    }

    public void deregister(){
        BubbleNetwork.getInstance().unregisterMenu(this);
    }

    private UUID uuid = null;
    public void click(Player player, ClickType type, int slot, ItemStack itemStack) {
        if (slot < mapList.size()) {
            GameMap map = mapList.get(slot);
            if (votes.get(player.getUniqueId()) == map) {
                votes.remove(player.getUniqueId());
                player.spigot().sendMessage(new MessageUtil.MessageBuilder("You cancelled your vote for ").color(ChatColor.BLUE).append(map.getName()).color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Joiner.on("\n" +ChatColor.GRAY + ChatColor.ITALIC.toString()).join(map.getDescription())))).create());
            } else {
                votes.put(player.getUniqueId(), map);
                player.spigot().sendMessage(new MessageUtil.MessageBuilder("You have voted for ").color(ChatColor.BLUE).append(map.getName()).color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Joiner.on("\n" + ChatColor.GRAY + ChatColor.ITALIC.toString()).join(map.getDescription())))).create());
            }
            player.playSound(player.getLocation(), click, 1f, 1f);
            uuid = player.getUniqueId();
            updateAll();
        }
    }

    @Override
    public ItemStack[] generate() {
        ItemStack[] generate = contents.clone();
        if(uuid != null && votes.containsKey(uuid)){
            int slot = slotMap.get(votes.get(uuid));
            ItemStackBuilder replacewith = new ItemStackBuilder(generate[slot].clone());
            replacewith.withType(Material.EMPTY_MAP);
            replacewith.withLore("",ChatColor.GOLD + "You have vote for this map");
            generate[slot] = EnchantGlow.addGlow(replacewith.build());
        }
        return generate;
    }
}
