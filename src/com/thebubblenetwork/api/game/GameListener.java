package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.maps.VoteInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

/**
 * Created by Jacob on 26/12/2015.
 */
public class GameListener implements Listener {
    private static ItemStackBuilder mapselection = new ItemStackBuilder(Material.PAPER).withName(ChatColor.DARK_AQUA
                                                                                                         + "" +
                                                                                                         ChatColor
                                                                                                                 .UNDERLINE + "Maps").withLore(ChatColor.GRAY + "Click to vote for a map").withAmount(1);
    private static ItemStackBuilder kitselection = new ItemStackBuilder(Material.IRON_AXE).withName(ChatColor
                                                                                                            .DARK_AQUA + "" + ChatColor.UNDERLINE + "Kits").withLore(ChatColor.GRAY + "Click to select or buy a kit").withAmount(1);
    private static Class<?> craftworld;
    private static Field chunkgeneratorfield;

    static {
        try {
            craftworld = ReflectionUTIL.getCraftClass("CraftWorld");
            chunkgeneratorfield = ReflectionUTIL.getField(craftworld, "generator", true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public GameListener() {
        BubbleGameAPI.getInstance().registerListener(this);
    }

    public static ItemStack[] generateSpawnInventory(int inventorysize) {
        ItemStack[] is = new ItemStack[inventorysize];
        is[2] = mapselection.build();
        is[6] = kitselection.build();
        return is;
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent e) {
        if (!BubbleGameAPI.getInstance().getState().joinable()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, BubbleNetwork.getPrefix() + "You cannot join at " +
                    "this time");
        }
        else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY && Bukkit.getOnlinePlayers()
                .size() == BubbleGameAPI.getInstance().getMaxPlayers()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, BubbleNetwork.getPrefix() + "This game is " +
                    "currently full");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().setContents(generateSpawnInventory(4 * 9));
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.setHealth(20.0D);
        p.setHealthScale(20.0D);
        p.setMaxHealth(20.0D);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setSaturation(600);
        Messages.sendMessageTitle(p, "", ChatColor.AQUA + "Welcome to " + ChatColor.BLUE + BubbleGameAPI.getInstance
                ().getName(), new Messages.TitleTiming(10, 20, 30));
        if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY) {
            p.teleport(BubbleGameAPI.getLobbySpawn().toLocation(Bukkit.getWorld("world")));
            p.setGameMode(GameMode.SURVIVAL);
            if (Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMinPlayers()) {
                BubbleGameAPI.getInstance().startWaiting();
            }
        }
        else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.INGAME) {
            //TODO - SPECTATING
            p.setGameMode(GameMode.ADVENTURE);
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    //TODO - Sending to LOBBY
                }
            }.runTask(BubbleGameAPI.getInstance());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMaxPlayers()){
            BubbleGameAPI.getInstance().cancelWaiting();
        }
    }

    public boolean canDefault() {
        return (BubbleGameAPI.getInstance().getState() != BubbleGameAPI.State.INGAME && BubbleGameAPI.getInstance()
                .getState() != BubbleGameAPI.State.ENDGAME);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldInit(WorldLoadEvent e) {
        World w = e.getWorld();
        if (w.getName().equals("world")) {
            w.getPopulators().clear();
            if (craftworld.isInstance(w)) {
                chunkgeneratorfield.setAccessible(true);
                try {
                    chunkgeneratorfield.set(w, VoidWorldGenerator.getGenerator());
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItems(PlayerDropItemEvent e) {
        if (canDefault())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItems(PlayerPickupItemEvent e) {
        if (canDefault())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (canDefault()) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            Player p = e.getPlayer();
            if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY &&
                    e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
                int slot = p.getInventory().getHeldItemSlot();
                if (slot == 2) {
                    VoteInventory.getVoteInventory().show(p);
                }
                else if (slot == 6) {
                    KitSelection.openMenu(p);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && canDefault())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamage(BlockDamageEvent e) {
        if (canDefault())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCanBlock(BlockCanBuildEvent e) {
        if (canDefault())
            e.setBuildable(false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLoseFood(FoodLevelChangeEvent e) {
        if (canDefault()) {
            if (e.getFoodLevel() == 20)
                e.setCancelled(true);
            else
                e.setFoodLevel(20);

        }
    }

}
