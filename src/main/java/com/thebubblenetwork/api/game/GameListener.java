package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.maps.VoteInventory;
import com.thebubblenetwork.api.global.type.ServerType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Jacob on 26/12/2015.
 */
public class GameListener implements Listener {
    private static ItemStackBuilder mapselection = new ItemStackBuilder(Material.PAPER)
            .withName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "Maps")
            .withLore(ChatColor.GRAY + "Click to vote for a map")
            .withAmount(1);
    private static ItemStackBuilder kitselection = new ItemStackBuilder(Material.IRON_AXE)
            .withName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "Kits")
            .withLore(ChatColor.GRAY + "Click to select or buy a kit")
            .withAmount(1);

    private static String ghostteam = "GHOST";

    private static int SPECTATORLOBBYSLOT = 8, SPECTATORPLAYERSSLOT = 0, MAPSLOT = 1, KITSLOT = 0, LOBBYSLOT = 8;

    private static ItemStackBuilder LOBBYITEM = new ItemStackBuilder(Material.WOOD_DOOR)
            .withName(ChatColor.DARK_RED + "Go back to the lobby")
            .withLore(ChatColor.RED + "Click this to go back to the lobby")
            .withAmount(1)
            .withGlow();

    private static ItemStackBuilder PLAYERS = new ItemStackBuilder(Material.COMPASS)
            .withName(ChatColor.DARK_AQUA + "Spectator menu")
            .withLore(ChatColor.GRAY + "Click this to open the spectator menu")
            .withAmount(1);
    private List<UUID> spectators = new ArrayList<>();
    private Map<Location, Inventory> chests = new HashMap<>();

    public GameListener() {
        BubbleGameAPI.getInstance().registerListener(this);
    }

    public static ItemStack[] generateSpawnInventory(int inventorysize) {
        ItemStack[] is = new ItemStack[inventorysize];
        is[MAPSLOT] = mapselection.build();
        is[KITSLOT] = kitselection.build();
        is[LOBBYSLOT] = LOBBYITEM.build();
        return is;
    }

    public boolean isSpectating(Player p) {
        return isSpectating(p.getUniqueId());
    }

    @Deprecated
    public boolean isSpectating(UUID u) {
        return spectators.contains(u);
    }

    public List<UUID> getSpectatorList() {
        return spectators;
    }

    public void cleanSpectators() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isSpectating(p)) {
                Team t = p.getScoreboard().getTeam(ghostteam);
                t.unregister();
            }
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(p);
                p.showPlayer(other);
            }
        }
    }

    public void setSpectating(Player p, boolean spectating) {
        if (spectating) enableSpectate(p);
        else disableSpectate(p);
    }

    private void startGhost(Player p) {
        Scoreboard board = p.getScoreboard();
        Team t = board.getTeam(ghostteam);
        if (t != null) t.unregister();
        ;
        t = board.registerNewTeam(ghostteam);
        t.setPrefix(ChatColor.RED.toString());
        t.setCanSeeFriendlyInvisibles(true);
        t.setAllowFriendlyFire(false);
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (isSpectating(other)) {
                t.addPlayer(other);
                other.getScoreboard().getTeam(ghostteam).addPlayer(p);
            }
        }
    }


    private void endGhost(Player p) {
        Scoreboard board = p.getScoreboard();
        Team t = board.getTeam(ghostteam);
        t.unregister();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (isSpectating(other)) {
                other.getScoreboard().getTeam(ghostteam).removePlayer(p);
            }
        }
    }

    private void enableSpectate(final Player p) {
        if (isSpectating(p)) return;
        spectators.add(p.getUniqueId());
        Entity temp = null;
        p.setGameMode(GameMode.ADVENTURE);
        for (Iterator<Entity> iterator = p.getNearbyEntities(100d, 100d, 100d).iterator(); iterator.hasNext(); temp = iterator.next()) {
            if (temp != null && temp instanceof Creature) {
                Creature c = (Creature) temp;
                if (c.getTarget() == p) {
                    c.setTarget(null);
                }
            }
        }
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (isSpectating(target)) {
                target.showPlayer(p);
            } else {
                target.hidePlayer(p);
            }
            p.showPlayer(target);
        }
        if (p.isDead()) p.spigot().respawn();
        startGhost(p);
        p.getInventory().setContents(new ItemStack[4 * 9]);
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.getInventory().setItem(SPECTATORLOBBYSLOT, LOBBYITEM.build());
        p.getInventory().setItem(SPECTATORPLAYERSSLOT, PLAYERS.build());
        p.setHealth(20d);
        p.setFoodLevel(20);
        p.setHealthScale(20d);
        p.setMaxHealth(20d);
        p.setLevel(0);
        p.setExp(0f);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.spigot().setCollidesWithEntities(false);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1), false);
        BubbleGameAPI.getInstance().getPlayerList().update();
        Messages.sendMessageTitle(p, "", ChatColor.AQUA + "You are now spectating", new Messages.TitleTiming(10, 30, 20));
    }

    private void disableSpectate(final Player p) {
        if (!isSpectating(p)) return;
        spectators.remove(p.getUniqueId());
        endGhost(p);
        p.spigot().setCollidesWithEntities(true);
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (isSpectating(target)) {
                p.hidePlayer(target);
            } else p.showPlayer(target);
            target.showPlayer(p);
        }
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        BubbleGameAPI.getInstance().getPlayerList().update();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMetaSet(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (p != t) {
                if (isSpectating(t)) {
                    p.hidePlayer(t);
                } else p.showPlayer(t);
            }
        }
    }

    @EventHandler
    public void onEntityTargetSpectator(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player && isSpectating((Player) e.getTarget())) e.setCancelled(true);
    }

    @EventHandler
    public void onCollideVehicleSpectator(VehicleEntityCollisionEvent e) {
        if (e.getEntity() instanceof Player && isSpectating((Player) e.getEntity())) {
            e.setCollisionCancelled(true);
            e.setPickupCancelled(true);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCloseInventorySpectator(InventoryCloseEvent e) {
        final Inventory i = e.getInventory();
        if (e.getPlayer() instanceof Player && isSpectating((Player) e.getPlayer())) {
            if (i.getHolder() != null && i.getHolder() instanceof Chest) {
                Chest c = (Chest) i.getHolder();
                final Location l = toBlockLocation(c.getLocation());
                if (chests.containsKey(l) && i.getViewers().size() <= 1) {
                    chests.remove(l);
                }
            }
        }
    }

    private Location toBlockLocation(Location l) {
        return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEditChest(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && !isSpectating((Player) e.getWhoClicked())) {
            final Inventory clicked = e.getView().getTopInventory();
            if (clicked == e.getView().getTopInventory() && clicked.getHolder() != null && clicked.getHolder() instanceof Chest) {
                Chest c = (Chest) clicked.getHolder();
                Location l = toBlockLocation(c.getLocation());
                if (chests.containsKey(l)) {
                    final Inventory change = chests.get(l);
                    BubbleGameAPI.getInstance().runTask(new Runnable() {
                        public void run() {
                            change.setContents(clicked.getContents());
                        }
                    });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEditChest(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player && !isSpectating((Player) e.getWhoClicked())) {
            final Inventory clicked = e.getView().getTopInventory();
            if (clicked == e.getView().getTopInventory() && clicked.getHolder() != null && clicked.getHolder() instanceof Chest) {
                Chest c = (Chest) clicked.getHolder();
                Location l = toBlockLocation(c.getLocation());
                if (chests.containsKey(l)) {
                    final Inventory change = chests.get(l);
                    BubbleGameAPI.getInstance().runTask(new Runnable(){
                        public void run() {
                            change.setContents(clicked.getContents());
                        }
                    });
                }
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player && (canDefault() || isSpectating((Player) e.getWhoClicked())))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && (canDefault() || isSpectating((Player) e.getWhoClicked())))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if (canDefault() || isSpectating(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (canDefault() || isSpectating(e.getPlayer())) e.setCancelled(true);
    }


    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        if (canDefault()) {
            e.setBuildable(false);
        } else {
            Block b = e.getBlock();
            Location loc = b.getLocation();
            for (Player p : b.getWorld().getPlayers()) {
                if (isSpectating(p) && p.getLocation().distanceSquared(loc) <= 2.0) {
                    e.setBuildable(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent e) {
        if (!BubbleGameAPI.getInstance().getState().joinable()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, BubbleNetwork.getPrefix() + "You cannot join at " +
                    "this time");
        } else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY && Bukkit.getOnlinePlayers()
                .size() == BubbleGameAPI.getInstance().getType().getMaxPlayers()) {
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
        } else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.INGAME) {
            setSpectating(p, true);
        } else {
            BubbleGameAPI.getInstance().runTask(new Runnable(){
                public void run() {
                    BubbleNetwork.getInstance().sendPlayer(p, ServerType.getType("Lobby"));
                }
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        setSpectating(e.getPlayer(), false);
        if (Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMinPlayers() && BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY) {
            BubbleGameAPI.getInstance().cancelWaiting();
        }
    }

    public boolean canDefault() {
        return (BubbleGameAPI.getInstance().getState() != BubbleGameAPI.State.INGAME);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (canDefault()) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY &&
                    e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
                int slot = p.getInventory().getHeldItemSlot();
                if (slot == MAPSLOT) {
                    VoteInventory.getVoteInventory().show(p);
                } else if (slot == KITSLOT) {
                    KitSelection.openMenu(p);
                } else if (slot == LOBBYSLOT) {
                    BubbleGameAPI.getInstance().getHubInventory().show(p);
                }
            }
        } else if (isSpectating(p)) {
            e.setCancelled(true);
            int i = p.getInventory().getHeldItemSlot();
            if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.PHYSICAL)) {
                if (i == SPECTATORLOBBYSLOT) BubbleGameAPI.getInstance().getHubInventory().show(p);
                else if (i == SPECTATORPLAYERSSLOT) BubbleGameAPI.getInstance().getPlayerList().show(p);
            } else {
                Block clicked = e.getClickedBlock();
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK && clicked != null) {
                    if (clicked.getType() == Material.CHEST || clicked.getType() == Material.TRAPPED_CHEST) {
                        BlockState clickedstate = clicked.getState();
                        if (clickedstate != null && clickedstate instanceof Chest) {
                            Chest chest = (Chest) clickedstate;
                            Location l = toBlockLocation(chest.getLocation());
                            Inventory inventory;
                            if (chests.containsKey(l)) {
                                inventory = chests.get(l);
                            } else {
                                inventory = Bukkit.createInventory(chest, chest.getInventory().getSize(), chest.getInventory().getName());
                                inventory.setContents(chest.getInventory().getContents());
                                chests.put(l, inventory);
                            }
                            p.openInventory(inventory);
                        }
                    } else {
                        BlockState state = clicked.getState();
                        if (state instanceof InventoryHolder) {
                            InventoryHolder holder = (InventoryHolder) state;
                            p.openInventory(holder.getInventory());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (canDefault()) e.setCancelled(true);
            else {
                final Player p = (Player) e.getEntity();
                if (isSpectating(p)) {
                    e.setCancelled(true);
                } else {
                    if (e instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) e;
                        if (damageByEntityEvent.getDamager() instanceof Player) {
                            Player damager = (Player) damageByEntityEvent.getDamager();
                            if (isSpectating(damager)) {
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                    BubbleGameAPI.getInstance().runTask(new Runnable(){
                        public void run() {
                            if (p.isOnline() && !isSpectating(p))
                                BubbleGameAPI.getInstance().getPlayerList().update(p);
                        }
                    });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamage(BlockDamageEvent e) {
        if (canDefault() || isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerFoodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && (canDefault() || isSpectating((Player) e.getEntity()))) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFoodChangeList(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && !isSpectating((Player) e.getEntity())) {
            final Player p = (Player) e.getEntity();
            BubbleGameAPI.getInstance().runTask(new Runnable() {
                public void run() {
                    if (p.isOnline() && !isSpectating(p)) BubbleGameAPI.getInstance().getPlayerList().update(p);
                }
            });
        }
    }
}
