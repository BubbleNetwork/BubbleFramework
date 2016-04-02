package com.thebubblenetwork.api.game;

import com.google.common.collect.ImmutableMap;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.anticheat.NCPManager;
import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.cosmetics.CosmeticsManager;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.messages.titlemanager.types.TimingTicks;
import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import com.thebubblenetwork.api.framework.plugin.util.BubbleRunnable;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BoardModule;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BoardPreset;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BoardScore;
import com.thebubblenetwork.api.framework.util.mc.timer.GameTimer;
import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.game.inventory.LobbyInventory;
import com.thebubblenetwork.api.game.kit.Kit;
import com.thebubblenetwork.api.game.kit.KitManager;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.listener.GameListener;
import com.thebubblenetwork.api.game.maps.GameMap;
import com.thebubblenetwork.api.game.maps.MapData;
import com.thebubblenetwork.api.game.maps.Vote;
import com.thebubblenetwork.api.game.maps.VoteInventory;
import com.thebubblenetwork.api.game.scoreboard.GameBoard;
import com.thebubblenetwork.api.game.scoreboard.LobbyPreset;
import com.thebubblenetwork.api.game.spectator.PlayersList;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.FileUTIL;
import com.thebubblenetwork.api.global.file.SSLUtil;
import com.thebubblenetwork.api.global.sql.SQLConnection;
import com.thebubblenetwork.api.global.sql.SQLUtil;
import com.thebubblenetwork.api.global.type.ServerType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Jacob on 12/12/2015.
 */
public abstract class BubbleGameAPI extends BubbleAddon {
    private static final String LOBBYMAP = "world.zip";

    public static Vector getLobbySpawn() {
        return new Vector(0D, 50D, 0D);
    }

    public static BubbleGameAPI getInstance() {
        return instance;
    }

    public static void setInstance(BubbleGameAPI instance) {
        BubbleGameAPI.instance = instance;
    }

    private static void stateChange(final BubbleGameAPI api, State oldstate, State newstate) {
        if (newstate.getPreset() != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                GameBoard.getBoard(p).enable(newstate.getPreset());
            }
        }
        if (newstate == State.PREGAME) {

            api.chosenmap = calculateMap(api);
            api.chosen = Bukkit.getWorld(api.chosenmap.getName());
            api.teleportPlayers(api.chosenmap, api.chosen);
            for (Player p : Bukkit.getOnlinePlayers()) {
                Kit k = KitSelection.getSelection(p).getKit();
                BukkitBubblePlayer player = BukkitBubblePlayer.getObject(p.getUniqueId());
                k.apply(player);
            }
            api.timer = new GameTimer(20, 5) {
                public void run(int seconds) {
                    Messages.broadcastMessageTitle(ChatColor.BLUE + String.valueOf(seconds), ChatColor.AQUA + "The game is starting", new TimingTicks(TimeUnit.MILLISECONDS,250, 500, 100));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation().getBlock().getLocation(), Sound.NOTE_BASS, 1f, 1f);
                    }
                }

                public void end() {
                    api.setState(State.INGAME);
                }
            };
        }

        Change:
        if (newstate == State.HIDDEN && oldstate == null) {
            //Lobby world folder
            File worldfolder = new File(lobbyworld);

            //Deleting if it already exists
            FileUTIL.deleteDir(worldfolder);

            //Allowing all SSL
            try {
                SSLUtil.allowAnySSL();
            } catch (Exception e) {
                BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not allow all SSL", e);
                break Change;
            }

            //Temp zip file
            File tempzip = new File("temp.zip");

            //Downloading
            try {
                DownloadUtil.download(tempzip, LOBBYMAP, BubbleNetwork.getInstance().getFileConnection());
            } catch (Exception e) {
                BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not download lobby", e);
                break Change;
            }
            FileUTIL.setPermissions(tempzip, true, true, true);

            //Unzipping into temp folder
            File temp = new File("temp");
            try {
                FileUTIL.unZip(tempzip.getPath(), temp.getPath());
            } catch (IOException e) {
                BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not unzip files", e);
                break Change;
            }
            //May be some problems deleting temp, lets see
            if (!tempzip.delete()) {
                System.gc();
                if (!tempzip.delete()) {
                    tempzip.deleteOnExit();
                }
            }
            //Extracting from temp folder
            try {
                FileUTIL.copy(new File(temp + File.separator + temp.list()[0]), worldfolder);
            } catch (IOException e) {
                BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not copy files", e);
                break Change;
            }
            FileUTIL.deleteDir(temp);
            FileUTIL.setPermissions(worldfolder,true,true,true);
            new WorldCreator(BubbleGameAPI.lobbyworld).generateStructures(false).generator(VoidWorldGenerator.getGenerator()).createWorld();

            //Getting ultracosmetics
            api.cosmeticsManager.download();
            api.cosmeticsManager.load();
            api.cosmeticsManager.enable();
        }
        if (newstate == State.LOADING) {
            //Load maps
            GameMap.doMaps();

            api.voteInventory = new VoteInventory(GameMap.getMaps().size());
            //Start lobby phase
            api.setState(State.LOBBY);
        }

        if (newstate == State.RESTARTING) {
            //Sending players to spawn
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(getLobbySpawn().toLocation(Bukkit.getWorld(lobbyworld)));
            }

            //Any cleanup tasks
            api.cleanup();

            if(api.getChosen() != null){
                World chosen = api.getChosen();
                File folder = chosen.getWorldFolder();
                Bukkit.unloadWorld(chosen, false);
                if(folder.exists()){
                    FileUTIL.deleteDir(folder);
                }
            }

            //Removing timer
            api.timer = null;

            //Removing chosen gamemap
            api.chosen = null;
            api.chosenmap = null;

            //Resetting votes
            api.getVotes().clear();
        }

        if (newstate == State.LOBBY) {
            Location spawn = getLobbySpawn().toLocation(Bukkit.getWorld(lobbyworld));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(spawn);
                p.getInventory().setContents(GameListener.generateSpawnInventory(4 * 9));
                p.getInventory().setArmorContents(new ItemStack[4]);
                p.setHealth(20.0D);
                p.setHealthScale(20.0D);
                p.setMaxHealth(20.0D);
                p.setFoodLevel(20);
                p.setLevel(0);
                p.setSaturation(600);
                Messages.sendMessageTitle(p, "", ChatColor.AQUA + "Welcome to " + ChatColor.BLUE + BubbleGameAPI.getInstance().getName(), new TimingTicks(TimeUnit.MILLISECONDS,500, 1000, 1500));
                p.teleport(BubbleGameAPI.getLobbySpawn().toLocation(Bukkit.getWorld(lobbyworld)));
                p.setGameMode(GameMode.SURVIVAL);
            }
            api.cancelWaiting();

            //After 10 seconds we check whether we can start the game again
            new BubbleRunnable(){
                public void run() {
                    if (api.getTimer() == null && Bukkit.getOnlinePlayers().size() >= api.getMinPlayers()) {
                        api.startWaiting();
                    }
                }
            }.runTaskLater(api,TimeUnit.SECONDS,10);
        }
    }

    private static GameMap calculateMap(BubbleGameAPI api) {
        final double chance = BubbleNetwork.getRandom().nextDouble();
        double current = 0;
        for (Map.Entry<GameMap, Double> entry : calculatePercentages(api).entrySet()) {
            current += entry.getValue();
            if (current <= chance) {
                return entry.getKey();
            }
        }
        //Hopefully shouldn't go past this point
        return GameMap.getMaps().get(0);
    }

    private static Map<GameMap, Double> calculatePercentages(BubbleGameAPI api) {
        Map<GameMap, Double> maps = new HashMap<>();
        final double votesize = api.getVotes().size();
        final double mapsize = GameMap.getMaps().size();
        for (Map.Entry<GameMap, Integer> entry : calculateScores(api).entrySet()) {
            maps.put(entry.getKey(), ((double) entry.getValue() + 1.0D) / (votesize + mapsize));
        }
        return maps;
    }

    private static Map<GameMap, Integer> calculateScores(BubbleGameAPI api) {
        Map<GameMap, Integer> maps = new HashMap<>();
        for (GameMap map : GameMap.getMaps()) {
            maps.put(map, 0);
        }
        GameMap temp;
        for (Vote v : api.getVotes().values()) {
            if ((temp = v.getMap()) != null && maps.containsKey(temp)) {
                maps.put(temp, maps.get(temp) + 1);
            }
        }
        return maps;
    }

    private static BubbleGameAPI instance;
    public static String lobbyworld = "Lobby";
    private LobbyPreset preset = new LobbyPreset();
    private World chosen = null;
    private GameMap chosenmap = null;
    private Map<UUID, Vote> votes = new HashMap<UUID, Vote>();
    private GameListener listener;
    private VoteInventory voteInventory;
    private GameTimer timer;
    private LobbyInventory hubInventory;
    private PlayersList list;
    private GameMode defaultgamemode;
    private String defaultkit;
    private String type;
    private int minplayers;
    private CosmeticsManager cosmeticsManager = new CosmeticsManager(BubbleNetwork.getInstance());
    private NCPManager cheatmanager = new NCPManager(BubbleNetwork.getInstance());

    public BubbleGameAPI(String type, GameMode defaultgamemode, String defaultkit, int minplayers) {
        super();
        this.minplayers = minplayers;
        this.type = type;
        this.defaultgamemode = defaultgamemode;
        this.defaultkit = defaultkit;
    }

    public int getMinPlayers() {
        return minplayers;
    }

    public GameListener getGame() {
        return listener;
    }

    public VoteInventory getVoteInventory() {
        return voteInventory;
    }

    public GameMap getChosenGameMap() {
        return chosenmap;
    }

    public World getChosen() {
        return chosen;
    }

    public void onLoad() {
        setState(State.HIDDEN);
    }

    public Map<GameMap, Double> calculatePercentages() {
        return calculatePercentages(this);
    }

    public State getState() {
        return State.state;
    }

    public void setState(State newstate) {
        State oldstate = State.state;
        State.state = newstate;
        onStateChange(oldstate, newstate);
        stateChange(this, oldstate, newstate);
    }

    public LobbyInventory getHubInventory() {
        return hubInventory;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public PlayersList getPlayerList() {
        return list;
    }

    public void onEnable() {
        setInstance(this);
        SQLConnection connection = BubbleNetwork.getInstance().getConnection();
        try {
            BubbleNetwork.getInstance().getLogger().log(Level.INFO, "Finding map table");
            if (!SQLUtil.tableExists(connection, MapData.maptable)) {
                BubbleNetwork.getInstance().getLogger().log(Level.INFO, "Map table not found, creating new");
                SQLUtil.createTable(connection, MapData.maptable, new ImmutableMap.Builder<String, Map.Entry<SQLUtil.SQLDataType, Integer>>().put("map", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, 32)).put("key", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, -1)).put("value", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, -1)).build());
                throw new IllegalArgumentException("Could not get map table");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not get map table");
        }
        KitSelection.register(this);
        listener = new GameListener();
        hubInventory = new LobbyInventory();
        list = new PlayersList();
        runTask(new Runnable() {
            public void run() {
                setState(State.LOADING);
            }
        });
        cheatmanager.download();
        cheatmanager.enable();
    }

    public void onDisable() {
        setState(State.RESTARTING);
        World w;
        if((w = Bukkit.getWorld(lobbyworld)) != null) {
            File folder = w.getWorldFolder();
            Bukkit.unloadWorld(w, false);
            FileUTIL.deleteDir(folder);
        }

        //Getting rid of cosmetics
        if(Bukkit.getPluginManager().isPluginEnabled("UltraCosmetics")) {
            try {
                cosmeticsManager.disable();
            }
            catch (Exception ex){
                //Silent
            }
        }

        //We don't want anything being thrown here
        try{
            cosmeticsManager.unload();
        }
        catch (Exception ex){
            //Silent
        }

        cosmeticsManager.clearUp();

        cheatmanager.disable();
        cheatmanager.unload();
        cheatmanager.clearUp();

        //Next addon may use gameapi
        GameMap.getMaps().clear();
        KitManager.getKits().clear();
        KitSelection.getMenuMap().clear();

        setInstance(null);
    }


    public void startWaiting() {
        if (timer != null) {
            return;
        }
        BoardModule module = getPreset().getModule("Status");
        for (GameBoard board : GameBoard.getBoards()) {
            BoardScore score = board.getScore(preset, module);
            score.getTeam().setSuffix(LobbyPreset.STARTING);
        }
        timer = new GameTimer(20, 20) {
            public void run(int seconds) {
                BoardModule module = preset.getModule("StatusValue");
                for (GameBoard board : GameBoard.getBoards()) {
                    BoardScore score = board.getScore(preset, module);
                    score.getTeam().setSuffix(String.valueOf(seconds));
                }
                if (seconds <= 3 || seconds % 5 == 0) {
                    Messages.broadcastMessageTitle(ChatColor.BLUE + String.valueOf(seconds), null, null);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation().getBlock().getLocation(), Sound.NOTE_BASS, 1f, 1f);
                }
            }

            public void end() {
                setState(State.PREGAME);
            }
        };
    }

    public void cancelWaiting() {
        if (timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
        BoardModule module = getPreset().getModule("Status");
        for (GameBoard board : GameBoard.getBoards()) {
            BoardScore score = board.getScore(getPreset(), module);
            score.getTeam().setSuffix(LobbyPreset.PLAYERNEED);
        }
    }

    public LobbyPreset getPreset() {
        return preset;
    }

    public abstract void cleanup();

    public Map<UUID, Vote> getVotes() {
        return votes;
    }

    public void resetVotes(UUID u) {
        getVotes().remove(u);
    }

    public void addVote(UUID u, GameMap vote) {
        if (getVotes().containsKey(u)) {
            getVotes().get(u).setVote(vote);
        }
    }

    public void win(final Player p) {
        if (getState() != State.INGAME) {
            return;
        }
        p.playSound(p.getLocation().getBlock().getLocation(), Sound.LEVEL_UP, 5F, 5F);
        Messages.broadcastMessageTitle(ChatColor.BLUE + p.getName(), ChatColor.AQUA + " has won the game", new TimingTicks(TimeUnit.SECONDS,1,2,3));
        setState(State.ENDGAME);
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (t != p) {
                t.teleport(p);
            }
        }
        timer = new GameTimer(5, 60) {
            public void run(int left) {
                if (!p.isOnline()) {
                    return;
                }
                getChosen().spigot().playEffect(p.getLocation(), Effect.FLAME);
                Random r = BubbleNetwork.getRandom();
                if (left % 20 == 0 || (left % 4 == 0 && left / 4 < 5)) {
                    Messages.broadcastMessageAction(ChatColor.BLUE + "Restarting in " + ChatColor.AQUA + String.valueOf(left / 4));
                    final Firework firework = getChosen().spawn(p.getLocation(), Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    Set<Color> colorSet = new HashSet<>();
                    int random = r.nextInt(3);
                    for (int i = 0; i < 2 + random; i++) {
                        colorSet.add(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                    }
                    meta.addEffect(FireworkEffect.builder().flicker(r.nextBoolean()).trail(r.nextBoolean()).withColor(colorSet).build());
                    firework.setFireworkMeta(meta);
                    firework.setVelocity(p.getLocation().getDirection().multiply(2 + r.nextInt(2)));
                    new BubbleRunnable() {
                        public void run() {
                            if (!firework.isDead()) {
                                firework.detonate();
                            }
                        }
                    }.runTaskLater(BubbleGameAPI.this, TimeUnit.SECONDS, 2 + r.nextInt(3));
                }
            }

            public void end() {
                if(p.isOnline()){
                    p.setAllowFlight(false);
                    p.setFlying(false);
                }
                GameMap chosen = getChosenGameMap();
                setState(State.RESTARTING);
                GameMap.extractMap(chosen);
                GameMap.setupMap(chosen);
                setState(State.LOBBY);
            }
        };
        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public void endGame(){
        setState(BubbleGameAPI.State.ENDGAME);
        new GameTimer(20, 15){
            public void run(int i) {
                if(i % 5 == 0 || i < 5)Messages.broadcastMessageAction(org.bukkit.ChatColor.DARK_AQUA + "Restarting in " + ChatColor.AQUA + i);
            }

            public void end(){
                GameMap chosen = getChosenGameMap();

                setState(State.RESTARTING);
                GameMap.extractMap(chosen);
                GameMap.setupMap(chosen);
                setState(State.LOBBY);
            }
        };
    }

    public CosmeticsManager getCosmeticsManager() {
        return cosmeticsManager;
    }

    public ServerType getType() {
        return ServerType.getType(type);
    }

    public Kit getDefaultKit() {
        return KitManager.getKit(defaultkit);
    }

    public abstract void onStateChange(State oldstate, State newstate);

    public abstract BoardPreset getScorePreset();

    public abstract GameMap loadMap(String name, MapData data, File yml, File zip);

    public abstract void teleportPlayers(GameMap map, World w);

    public String getTablesuffix() {
        return getName().toLowerCase();
    }

    public GameMode getGameMode() {
        return defaultgamemode;
    }

    public enum State {
        HIDDEN, LOADING, LOBBY, PREGAME, INGAME, ENDGAME, RESTARTING;

        protected static State state = null;

        public boolean joinable() {
            return this == LOBBY || this == INGAME;
        }

        public BoardPreset getPreset() {
            if (this == LOBBY) {
                return BubbleGameAPI.getInstance().getPreset();
            } else if (this == PREGAME || this == INGAME || this == ENDGAME) {
                return BubbleGameAPI.getInstance().getScorePreset();
            }
            return null;
        }
    }
}
