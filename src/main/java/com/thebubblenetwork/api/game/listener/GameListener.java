package com.thebubblenetwork.api.game.listener;

import com.thebubblenetwork.api.framework.event.PlayerDataReceivedEvent;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.plugin.util.BubbleRunnable;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BoardPreset;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.maps.VoteMenu;
import com.thebubblenetwork.api.game.scoreboard.GameBoard;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import com.thebubblenetwork.api.global.type.ServerType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
    public static ItemStack[] generateSpawnInventory(int inventorysize) {
        ItemStack[] is = new ItemStack[inventorysize];
        is[MAPSLOT] = mapselection.build();
        is[KITSLOT] = kitselection.build();
        is[LOBBYSLOT] = LOBBYITEM.build();
        return is;
    }

    private static ItemStackBuilder mapselection = new ItemStackBuilder(Material.PAPER).withName(ChatColor.DARK_AQUA + "Maps").withLore(ChatColor.GRAY + "Click to vote for a map");
    private static ItemStackBuilder kitselection = new ItemStackBuilder(Material.IRON_AXE).withName(ChatColor.DARK_AQUA + "Kits").withLore(ChatColor.GRAY + "Click to select or buy a kit");
    private static String ghostteam = "GHOST";
    private static int SPECTATORLOBBYSLOT = 8, SPECTATORPLAYERSSLOT = 0, MAPSLOT = 1, KITSLOT = 0, LOBBYSLOT = 8;
    private static ItemStackBuilder LOBBYITEM = new ItemStackBuilder(Material.WOOD_DOOR).withName(ChatColor.DARK_RED + "Go back to the lobby").withLore(ChatColor.RED + "Click to go back to the lobby").withAmount(1).withGlow();
    private static ItemStackBuilder PLAYERS = new ItemStackBuilder(Material.COMPASS).withName(ChatColor.DARK_AQUA + "Spectator menu").withLore(ChatColor.GRAY + "Click to open the spectator menu").withAmount(1);
    private List<UUID> spectators = Collections.synchronizedList(new ArrayList<UUID>());
    private Map<Location, Inventory> chests = new HashMap<>();

    public GameListener() {
        BubbleGameAPI.getInstance().registerListener(this);
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
        if (spectating) {
            enableSpectate(p);
        } else {
            disableSpectate(p);
        }
        BukkitBubblePlayer.getObject(p.getUniqueId()).setSpectating(spectating);
    }

    private void startGhost(Player p) {
        Scoreboard board = p.getScoreboard();
        Team t = board.getTeam(ghostteam);
        if (t != null) {
            t.unregister();
        }
        t = board.registerNewTeam(ghostteam);
        t.setCanSeeFriendlyInvisibles(true);
        t.setPrefix(ChatColor.GRAY + "[SPEC]");
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
        if (isSpectating(p)) {
            return;
        }
        spectators.add(p.getUniqueId());
        p.setGameMode(GameMode.ADVENTURE);
        for(Monster e:p.getWorld().getEntitiesByClass(Monster.class)){
            if(e.getTarget() == p)e.setTarget(null);
        }
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (isSpectating(target)) {
                target.showPlayer(p);
            } else {
                target.hidePlayer(p);
            }
            p.showPlayer(target);
        }
        if (p.isDead()) {
            p.spigot().respawn();
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
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
        Messages.sendMessageTitle(p, "", ChatColor.AQUA + "You are now spectating", null);
    }

    private void disableSpectate(final Player p) {
        if (!isSpectating(p)) {
            return;
        }
        spectators.remove(p.getUniqueId());
        endGhost(p);
        p.spigot().setCollidesWithEntities(true);
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (isSpectating(target)) {
                p.hidePlayer(target);
            } else {
                p.showPlayer(target);
            }
            target.showPlayer(p);
        }
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        BubbleGameAPI.getInstance().getPlayerList().update();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId() != t.getUniqueId()) {
                if (isSpectating(t)) {
                    p.hidePlayer(t);
                } else {
                    p.showPlayer(t);
                }
            }
        }
    }



    @EventHandler
    public void onEntityTargetSpectator(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player && isSpectating((Player) e.getTarget())) {
            e.setCancelled(true);
        }
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


    @EventHandler
    public void onPlayerVoidFall(PlayerMoveEvent e){
        //Player is about to fall into the void
        Player p = e.getPlayer();
        if(e.getTo().getY() < 0) {
            e.setCancelled(true);
            if(isSpectating(p)){
                //Facing direction, with upward Y
                p.setVelocity(p.getLocation().getDirection().multiply(2.0D).setY(5.0D));
            }
            else {
                switch (BubbleGameAPI.getInstance().getState()) {
                    case LOBBY:
                        p.teleport(BubbleGameAPI.getLobbySpawn().toLocation(Bukkit.getWorld(BubbleGameAPI.lobbyworld)));
                        break;
                    case INGAME:
                        //Fake Damage cause
                        p.setLastDamageCause(new EntityDamageEvent(p, EntityDamageEvent.DamageCause.VOID, p.getHealth()));
                        p.setHealth(0.0D);
                        break;
                    case ENDGAME:
                        //Facing direction, with upward Y
                        p.setVelocity(p.getLocation().getDirection().multiply(2.0D).setY(5.0D));
                    default:
                        break;
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
                    BubbleGameAPI.getInstance().runTask(new Runnable() {
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
        if (e.getWhoClicked() instanceof Player && (canDefault(true) || isSpectating((Player) e.getWhoClicked()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && (canDefault(true) || isSpectating((Player) e.getWhoClicked()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if (canDefault(false) || isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (canDefault(false) || isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        if (canDefault(false)) {
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
        } else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY && Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getType().getMaxPlayers()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, BubbleNetwork.getPrefix() + "This game is " +
                    "currently full");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        BukkitBubblePlayer player = BukkitBubblePlayer.getObject(e.getPlayer().getUniqueId());
        final GameBoard scoreboard = new GameBoard(player.getPlayer());
        GameBoard.setBoard(p, scoreboard);
        BoardPreset preset = BubbleGameAPI.getInstance().getState().getPreset();
        if (preset != null) {
            scoreboard.enable(preset);
        }
        player.getPlayer().setScoreboard(scoreboard.getObject().getBoard());
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().setContents(generateSpawnInventory(4 * 9));
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.setHealth(20.0D);
        p.setHealthScale(20.0D);
        p.setMaxHealth(20.0D);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setSaturation(600);
        Messages.sendMessageTitle(p, "", ChatColor.AQUA + "Welcome to " + ChatColor.BLUE + BubbleGameAPI.getInstance().getName(), null);
        if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY) {
            p.teleport(BubbleGameAPI.getLobbySpawn().toLocation(Bukkit.getWorld(BubbleGameAPI.lobbyworld)));
            p.setGameMode(GameMode.SURVIVAL);
            if (Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMinPlayers()) {
                BubbleGameAPI.getInstance().startWaiting();
            }
        } else if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.INGAME) {
            setSpectating(p, true);
        } else {
            BubbleGameAPI.getInstance().runTask(new Runnable() {
                public void run() {
                    BubbleNetwork.getInstance().sendPlayer(p, ServerType.getType("Lobby"));
                }
            });
        }
        new BubbleRunnable(){
            @Override
            public void run() {
                if(p.isOnline()) {
                    BukkitBubblePlayer player = BukkitBubblePlayer.getObject(p.getUniqueId());
                    if (player != null) {
                        for(GameBoard other: GameBoard.getBoards()){
                            other.applyRank(player.getRank(),p);
                        }
                    }
                    for(BubblePlayer other:BukkitBubblePlayer.getPlayerObjectMap().values()){
                        scoreboard.applyRank(other.getRank(),(Player)other.getPlayer());
                    }
                    if(isSpectating(p)){
                        new BubbleRunnable(){
                            public void run() {
                                setSpectating(p, false);
                                setSpectating(p, true);
                            }
                        }.runTask(BubbleGameAPI.getInstance());
                    }
                }
            }
        }.runTaskAsynchonrously(BubbleGameAPI.getInstance());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        setSpectating(e.getPlayer(), false);
        if (Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMinPlayers() && BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY) {
            BubbleGameAPI.getInstance().cancelWaiting();
        }
        else if(Bukkit.getOnlinePlayers().size() == getSpectatorList().size() && BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.INGAME){
            BubbleGameAPI.getInstance().endGame();
        }
        GameBoard.removeBoard(p);
        VoteMenu.removeMenu(p);
        new BubbleRunnable(){
            @Override
            public void run(){
                for(GameBoard other:GameBoard.getBoards()){
                    for(Team t:other.getObject().getBoard().getTeams()){
                        t.removePlayer(p);
                    }
                }
            }
        }.runTaskAsynchonrously(BubbleGameAPI.getInstance());
    }

    public boolean canDefault(boolean pregame) {
        return (BubbleGameAPI.getInstance().getState() != BubbleGameAPI.State.INGAME && (!pregame || BubbleGameAPI.getInstance().getState() != BubbleGameAPI.State.PREGAME));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (canDefault(false)) {
            if(canDefault(true)){
                e.setCancelled(true);
            }
            else {
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.ALLOW);
            }
            if (BubbleGameAPI.getInstance().getState() == BubbleGameAPI.State.LOBBY &&
                    e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
                int slot = p.getInventory().getHeldItemSlot();
                if (slot == MAPSLOT) {
                    VoteMenu.getMenu(p).show(p);
                } else if (slot == KITSLOT) {
                    KitSelection.openMenu(p);
                } else if (slot == LOBBYSLOT) {
                    BubbleGameAPI.getInstance().getHubInventory().show(p);
                }
            }
        } else if (isSpectating(p)) {
            e.setCancelled(true);
            int i = p.getInventory().getHeldItemSlot();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.PHYSICAL) {
                if (i == SPECTATORLOBBYSLOT) {
                    BubbleGameAPI.getInstance().getHubInventory().show(p);
                } else if (i == SPECTATORPLAYERSSLOT) {
                    BubbleGameAPI.getInstance().getPlayerList().show(p);
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
                                    inventory = Bukkit.createInventory(chest, chest.getInventory().getType());
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e) {
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
        if (e.getEntity() instanceof Player) {
            if (canDefault(true)) {
                e.setCancelled(true);
            } else {
                final Player p = (Player) e.getEntity();
                if (isSpectating(p)) {
                    e.setCancelled(true);
                }
                else {
                    BubbleGameAPI.getInstance().runTask(new Runnable() {
                        public void run() {
                            if (p.isOnline() && !isSpectating(p)) {
                                BubbleGameAPI.getInstance().getPlayerList().update(p);
                            }
                        }
                    });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamage(BlockDamageEvent e) {
        if (canDefault(true) || isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerFoodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && (canDefault(true) || isSpectating((Player) e.getEntity()))) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFoodChangeList(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && !isSpectating((Player) e.getEntity())) {
            final Player p = (Player) e.getEntity();
            BubbleGameAPI.getInstance().runTask(new Runnable() {
                public void run() {
                    if (p.isOnline() && !isSpectating(p)) {
                        BubbleGameAPI.getInstance().getPlayerList().update(p);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerDataReceived(PlayerDataReceivedEvent e){
        GameBoard board = GameBoard.getBoard(e.getPlayer());
        new BubbleRunnable(){
            public void run() {
                if(e.getPlayer().isOnline() && board.getCurrentpreset() != null)board.getCurrentpreset().onEnable(board);

            }
        }.runTaskAsynchonrously(BubbleGameAPI.getInstance());
    }
}
