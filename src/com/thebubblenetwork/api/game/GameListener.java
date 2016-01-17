package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.maps.VoteInventory;
import com.thebubblenetwork.api.game.spectator.SpectatorCheck;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Jacob on 26/12/2015.
 */
public class GameListener implements Listener {
    private static ItemStackBuilder mapselection = new ItemStackBuilder(Material.PAPER)
            .withName(ChatColor.DARK_AQUA+ "" + ChatColor.UNDERLINE + "Maps")
            .withLore(ChatColor.GRAY + "Click to vote for a map")
            .withAmount(1);
    private static ItemStackBuilder kitselection = new ItemStackBuilder(Material.IRON_AXE)
            .withName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "Kits")
            .withLore(ChatColor.GRAY + "Click to select or buy a kit")
            .withAmount(1);

    public GameListener() {
        BubbleGameAPI.getInstance().registerListener(this);
    }

    public static ItemStack[] generateSpawnInventory(int inventorysize) {
        ItemStack[] is = new ItemStack[inventorysize];
        is[2] = mapselection.build();
        is[6] = kitselection.build();
        return is;
    }

    private static List<UUID> spectators = new ArrayList<>();
    //private static Map<InventoryHolder,Inventory> chests = new HashMap<>();

    public static boolean isSpectating(Player p){
        return isSpectating(p.getUniqueId());
    }

    @Deprecated
    public static boolean isSpectating(UUID u){
        return spectators.contains(u);
    }

    public static Collection<Player> getSpectators(){
        List<Player> players = new ArrayList<>();
        for(Player p:Bukkit.getOnlinePlayers())if(isSpectating(p))players.add(p);
        return players;
    }

    public static void setSpectating(Player p,boolean spectating){
        if(spectating)enableSpectate(p);
        else disableSpectate(p);
    }

    private static void enableSpectate(Player p){
        if(isSpectating(p))return;
        Entity temp = null;
        p.setGameMode(GameMode.ADVENTURE);
        for (Iterator<Entity> iterator = p.getNearbyEntities(100d,100d,100d).iterator();iterator.hasNext();temp = iterator.next()) {
            if(temp != null && temp instanceof Creature){
                Creature c = (Creature)temp;
                if(c.getTarget() == p){
                    c.setTarget(null);
                }
            }
        }
        for(Player target:Bukkit.getOnlinePlayers()){
            if(target != p && target.canSee(p))target.hidePlayer(p);
        }
        spectators.add(p.getUniqueId());
        if(p.isDead())p.spigot().respawn();
        p.getInventory().setContents(new ItemStack[4*9]);
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.setHealth(20d);
        p.setFoodLevel(20);
        p.setHealthScale(20d);
        p.setMaxHealth(20d);
        p.setLevel(0);
        p.setExp(0f);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.spigot().setCollidesWithEntities(false);
        Messages.sendMessageTitle(p,"",ChatColor.AQUA + "You are now spectating",new Messages.TitleTiming(10,30,20));
    }

    private static void disableSpectate(Player p){
        if(!isSpectating(p))return;
        p.spigot().setCollidesWithEntities(true);
        for(Player t:Bukkit.getOnlinePlayers()){
            if(p != t){
                if(!t.canSee(p))t.showPlayer(p);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMetaSet(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.setMetadata("spectating",new LazyMetadataValue(BubbleGameAPI.getInstance(),LazyMetadataValue.CacheStrategy.NEVER_CACHE,new SpectatorCheck(p)));
        p.setMetadata("vanished",new LazyMetadataValue(BubbleGameAPI.getInstance(),LazyMetadataValue.CacheStrategy.NEVER_CACHE,new SpectatorCheck(p)));
        for(Player t:Bukkit.getOnlinePlayers()){
            if(p != t){
                if(isSpectating(t)){
                    if(p.canSee(t))p.hidePlayer(t);
                }
                else if(!p.canSee(t)){
                    p.showPlayer(t);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetSpectator(EntityTargetEvent e){
        if(e.getTarget() instanceof Player && isSpectating((Player)e.getTarget()))e.setCancelled(true);
    }

    @EventHandler
    public void onCollideVehicleSpectator(VehicleEntityCollisionEvent e){
        if(e.getEntity() instanceof Player && isSpectating((Player)e.getEntity())){
            e.setCollisionCancelled(true);
            e.setPickupCancelled(true);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroyVehicleSpectator(VehicleDestroyEvent e){
        if(e.getAttacker() instanceof Player && isSpectating((Player)e.getAttacker()))e.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamageSpectator(BlockDamageEvent e){
        if(isSpectating(e.getPlayer())){
            e.setInstaBreak(true);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakSpectator(HangingBreakByEntityEvent e){
        if(e.getRemover() instanceof Player && isSpectating((Player)e.getRemover()))e.setCancelled(true);
    }

    @EventHandler
    public void onPickupItemSpectator(PlayerPickupItemEvent e){
        if(isSpectating(e.getPlayer()))e.setCancelled(true);
    }

    @EventHandler
    public void onDropItemSpectator(PlayerDropItemEvent e){
        if(isSpectating(e.getPlayer()))e.setCancelled(true);
    }

    @EventHandler
    public void onSpectatorInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(isSpectating(p)){
            e.setCancelled(true);
            Block clicked = e.getClickedBlock();
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK && clicked != null){
                /*
                if(clicked.getType() == Material.CHEST || clicked.getType() == Material.TRAPPED_CHEST) {
                    BlockState clickedstate = clicked.getState();
                    if (clickedstate != null && clickedstate instanceof Chest) {
                        Chest chest = (Chest) clickedstate;
                        Inventory i;
                        if (chests.containsKey(chest)) {
                            i = chests.get(chest);
                        }
                        else {
                            i = Bukkit.createInventory(chest, chest.getInventory().getType());
                            i.setContents(chest.getInventory().getContents());
                            chests.put(chest, i);
                        }
                        p.openInventory(i);
                    }
                }
                */
                BlockState state = clicked.getState();
                if(state instanceof InventoryHolder){
                    InventoryHolder holder = (InventoryHolder)state;
                    p.openInventory(holder.getInventory());
                }
            }
        }
    }
/*
    @EventHandler
    public void onEntitySpectatorDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            if(isSpectating(p)){
                e.setCancelled(true);
                if(e instanceof EntityDamageByEntityEvent){
                    EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)e;
                    if(entityEvent.getDamager() instanceof Projectile){
                        Projectile projectile = (Projectile)entityEvent.getDamager();
                        Projectile fire;
                        if(projectile instanceof Arrow){
                            Arrow previous = (Arrow)projectile;
                            Arrow arrow = previous.getWorld().spawnArrow(previous.getLocation(),previous.getVelocity(),1f,1f);
                            arrow.setCritical(previous.isCritical());
                            arrow.setKnockbackStrength(previous.getKnockbackStrength());
                            arrow.spigot().setDamage(previous.spigot().getDamage());
                        }
                        else{
                            fire = (Projectile)projectile.getWorld().spawnEntity(projectile.getLocation(),projectile.getType());
                            fire.setVelocity(projectile.getVelocity());
                        }
                        fire = (Projectile)projectile.getWorld().spawnEntity(projectile.getLocation(),projectile.getType());
                        fire.setVelocity(projectile.getVelocity());
                        fire.setShooter(projectile.getShooter());
                        fire.setBounce(projectile.doesBounce());
                    }
                }
            }
        }
    }
*/
    /*
    @EventHandler
    public void onCloseInventorySpectator(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player && isSpectating((Player)e.getPlayer())){
            if(chests.containsKey(e.getInventory().getHolder())){
                if(e.getInventory().getViewers().size() == 0){
                    chests.remove(e.getInventory().getHolder());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEditChest(InventoryClickEvent e){
        final Inventory clicked = e.getClickedInventory();
        if(chests.containsKey(clicked.getHolder())){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(chests.containsKey(clicked.getHolder()))chests.get(clicked.getHolder()).setContents(clicked.getContents());
                }
            }.runTask(BubbleGameAPI.getInstance());
        }
    }
    */

    @EventHandler
    public void onPlayerDeathToSpectator(PlayerDeathEvent e){
        if(BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.INGAME)setSpectating(e.getEntity(),true);
    }

    @EventHandler
    public void onInventoryDragSpectator(InventoryDragEvent e){
        if(e.getWhoClicked() instanceof Player && isSpectating((Player)e.getWhoClicked()))e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickSpectator(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player && isSpectating((Player)e.getWhoClicked()))e.setCancelled(true);
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
            setSpectating(p,true);
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
        if(Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMaxPlayers() && BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY){
            BubbleGameAPI.getInstance().cancelWaiting();
        }
    }

    public boolean canDefault() {
        return (BubbleGameAPI.getInstance().getState() != BubbleGameAPI.State.INGAME);
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
