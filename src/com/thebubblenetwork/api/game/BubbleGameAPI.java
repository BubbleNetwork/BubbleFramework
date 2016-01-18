package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BubblePlayer;
import com.thebubblenetwork.api.framework.messages.Messages;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import com.thebubblenetwork.api.framework.ranks.Rank;
import com.thebubblenetwork.api.framework.util.files.FileUTIL;
import com.thebubblenetwork.api.framework.util.http.DownloadUtil;
import com.thebubblenetwork.api.framework.util.http.SSLUtil;
import com.thebubblenetwork.api.framework.util.java.DateUtil;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.BoardModule;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.BoardPreset;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.BoardScore;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.BubbleBoardAPI;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.util.BoardModuleBuilder;
import com.thebubblenetwork.api.framework.util.sql.SQLConnection;
import com.thebubblenetwork.api.framework.util.sql.SQLUtil;
import com.thebubblenetwork.api.game.kit.Kit;
import com.thebubblenetwork.api.game.kit.KitSelection;
import com.thebubblenetwork.api.game.maps.GameMap;
import com.thebubblenetwork.api.game.maps.MapData;
import com.thebubblenetwork.api.game.maps.Vote;
import com.thebubblenetwork.api.game.maps.VoteInventory;
import com.thebubblenetwork.api.game.scoreboard.GameBoard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jacob on 12/12/2015.
 */
public abstract class BubbleGameAPI extends BubblePlugin {
    private static final String playingtitle = ChatColor.BLUE + "" + ChatColor.BOLD + "Playing", ranktitle =
            ChatColor.BLUE + "" + ChatColor.BOLD + "Rank", tokenstitle = ChatColor.BLUE + "" + ChatColor.BOLD +
            "Tokens", site = "thebubblenetwork",playerneed = "Players needed", Starting = "Starting in";
    private static BubbleGameAPI instance;
    private static final String LOBBYMAP = "https://www.dropbox.com/s/0f6o78rpvd2oka3/world.zip?dl=1";

    private static BoardPreset LOBBY =
            new BoardPreset("Lobby",
                            new BoardModuleBuilder("Playing", 12)
                                    .withDisplay(playingtitle)
                                    .build(),
                            new BoardModuleBuilder("PlayingValue", 11)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Spacer1", 10)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Rank", 9)
                                    .withDisplay(ranktitle)
                                    .build(),
                            new BoardModuleBuilder("RankValue", 8)
                                    .withRandomDisplay().
                                    build(),
                            new BoardModuleBuilder("Spacer2", 7)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Tokens", 6)
                                    .withDisplay(tokenstitle)
                                    .build(),
                            new BoardModuleBuilder("TokensValue", 5)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Spacer3", 4)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Status", 3)
                                    .withDisplay(ChatColor.BLUE.toString() + ChatColor.BOLD.toString())
                                    .build()
                    , new BoardModuleBuilder("StatusValue", 2)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("Spacer4", 1)
                                    .withRandomDisplay()
                                    .build(),
                            new BoardModuleBuilder("site", 0)
                                    .withDisplay(site)
                                    .build()
            ) {

        public void onEnable(BubbleBoardAPI board) {
            BubblePlayer player = BubblePlayer.get(Bukkit.getPlayer(board.getName()));
            BoardScore playingValue = board.getScore(this, getModule("PlayingValue"));
            playingValue.getTeam().setSuffix(BubbleGameAPI.getInstance().getName());
            BoardScore rankValue = board.getScore(this, getModule("RankValue"));
            Rank r = player.getRank();
            rankValue.getTeam().setSuffix(r.isDefault() ? "No rank" : r.getName().charAt(0) + r.getName().substring(1));
            BoardScore tokenValue = board.getScore(this, getModule("TokensValue"));
            tokenValue.getTeam().setSuffix(String.valueOf(player.getTokens()));
            BoardScore status = board.getScore(this, getModule("Status"));
            BoardScore statusvalue = board.getScore(this, getModule("StatusValue"));
            if (Bukkit.getOnlinePlayers().size() < BubbleGameAPI.getInstance().getMinPlayers()) {
                status.getTeam().setSuffix(playerneed);
                statusvalue.getTeam().setSuffix(String.valueOf(BubbleGameAPI.getInstance().getMinPlayers() - Bukkit
                        .getOnlinePlayers().size()));
            }
            else {
                status.getTeam().setSuffix(Starting);
            }
            BoardScore site = board.getScore(this, getModule("site"));
            site.getTeam().setPrefix(ChatColor.GRAY + "play.");
            site.getTeam().setSuffix(ChatColor.GRAY + ".com");
        }
    };

    private World chosen = null;
    private GameMap chosenmap = null;
    private Map<UUID, Vote> votes = new HashMap<UUID, Vote>();
    private GameListener listener;
    private VoteInventory voteInventory;
    private GameTimer timer;

    public GameListener getGame(){
        return listener;
    }

    public VoteInventory getVoteInventory(){
        return voteInventory;
    }

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
        if (newstate.getPreset() != null) for (Player p : Bukkit.getOnlinePlayers()) {
                GameBoard.getBoard(p).enable(newstate.getPreset());
        }
        if (newstate == State.LOBBY && oldstate != State.HIDDEN && oldstate != State.LOADING) {
            api.cleanup();
        }
        if (newstate == State.PREGAME) {
            api.chosenmap = calculateMap(api);
            api.chosen = Bukkit.getWorld(api.chosenmap.getName());
            for (World w : Bukkit.getWorlds()) {
                if (!w.getName().equals("world") && !w.getName().equals(api.chosenmap.getName()))
                    Bukkit.unloadWorld(w, false);
            }
            api.teleportPlayers(api.chosenmap, api.chosen);
            api.timer = new GameTimer(20,5) {
                public void run(int seconds) {
                    Messages.broadcastMessageTitle(ChatColor.BLUE + String.valueOf(seconds),ChatColor.AQUA + "The game is starting",new Messages.TitleTiming(5,10,2));
                    for(Player p:Bukkit.getOnlinePlayers())p.playSound(p.getLocation().getBlock().getLocation(), Sound.NOTE_BASS,1f,1f);
                }

                public void end() {
                    api.setState(State.INGAME);
                }
            };
        }

        Change:if (newstate == State.HIDDEN && oldstate == null) {
            File worldfolder = new File("world");
            FileUTIL.deleteDir(worldfolder);
            File tempzip = new File("temp.zip");
            try {
                SSLUtil.allowAnySSL();
            } catch (Exception e) {
                //Automatic Catch Statement
                e.printStackTrace();
                break Change;
            }
            try {
                DownloadUtil.download(tempzip, LOBBYMAP);
            } catch (Exception e) {
                //Automatic Catch Statement
                e.printStackTrace();
                break Change;
            }
            FileUTIL.setPermissions(tempzip, true, true, true);
            File temp = new File("temp");
            try {
                FileUTIL.unZip(tempzip.getPath(), temp.getPath());
            } catch (IOException e) {
                //Automatic Catch Statement
                e.printStackTrace();
                break Change;
            }
            tempzip.delete();
            FileUTIL.setPermissions(temp, true, true, true);
            try {
                FileUTIL.copy(new File(temp + File.separator + "world"), worldfolder);
            } catch (IOException e) {
                //Automatic Catch Statement
                e.printStackTrace();
                break Change;
            }
            FileUTIL.deleteDir(temp);
        }
        if (newstate == State.LOADING) {
            World w = Bukkit.getWorld("world");
            w.setAutoSave(false);
            GameMap.doMaps();
            api.setState(State.LOBBY);
        }

        if (newstate == State.LOBBY) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(getLobbySpawn().toLocation(Bukkit.getWorld("world")));
                p.setGameMode(GameMode.SURVIVAL);
                p.getInventory().setContents(GameListener.generateSpawnInventory(4 * 9));
                p.getInventory().setArmorContents(new ItemStack[4]);
                p.setHealth(20.0D);
                p.setHealthScale(20.0D);
                p.setMaxHealth(20.0D);
                p.setFoodLevel(20);
                p.setLevel(0);
                p.setSaturation(600);
                Messages.sendMessageTitle(p, "", ChatColor.AQUA + "Welcome to " + ChatColor.BLUE + BubbleGameAPI
                        .getInstance().getName(), new Messages.TitleTiming(10, 20, 30));
                p.teleport(BubbleGameAPI.getLobbySpawn().toLocation(Bukkit.getWorld("world")));
                p.setGameMode(GameMode.SURVIVAL);
            }
            if (Bukkit.getOnlinePlayers().size() == BubbleGameAPI.getInstance().getMinPlayers()) {
                BubbleGameAPI.getInstance().setState(BubbleGameAPI.State.PREGAME);
            }
        }
    }

    private static GameMap calculateMap(BubbleGameAPI api) {
        final double chance = BubbleNetwork.getRandom().nextDouble();
        double current = 0;
        for (Map.Entry<GameMap, Double> entry : calculatePercentages(api).entrySet()) {
            current += entry.getValue();
            if (current <= chance)
                return entry.getKey();
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
        for (GameMap map : GameMap.getMaps())
            maps.put(map, 0);
        GameMap temp;
        for (Vote v : api.getVotes().values()) {
            if ((temp = v.getMap()) != null && maps.containsKey(temp))
                maps.put(temp, maps.get(temp) + 1);
        }
        return maps;
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
        stateChange(this, oldstate, newstate);
        onStateChange(oldstate, newstate);
    }

    public void onEnable() {
        setInstance(this);
        SQLConnection connection = BubbleNetwork.getInstance().getConnection();
        try {
            if (!SQLUtil.tableExists(connection, MapData.maptable)) {
                Map<String, Map.Entry<SQLUtil.SQLDataType, Integer>> map = new HashMap<>();
                map.put("map", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, 32));
                map.put("key", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                map.put("value", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                SQLUtil.createTable(connection, MapData.maptable, map);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        KitSelection.register(this);
        GameBoard.registerlistener(this);
        listener = new GameListener();
        voteInventory = new VoteInventory(9);
        new BukkitRunnable() {
            @Override
            public void run() {
                setState(State.LOADING);
            }
        }.runTask(this);
    }

    public void onDisable() {
        setState(State.RESTARTING);
        chosen = null;
        setInstance(null);
    }


    public void startWaiting(){
        if(timer != null)return;
        final BoardPreset preset = LOBBY;
        BoardModule module = preset.getModule("Status");
        for(GameBoard board:GameBoard.getBoards()){
            BoardScore score = board.getScore(preset,module);
            score.getTeam().setSuffix(Starting);
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.SECOND,30);
        timer = new GameTimer(20,20) {
            public void run(int seconds) {
                BoardModule module = preset.getModule("StatusValue");
                for(GameBoard board:GameBoard.getBoards()){
                    BoardScore score = board.getScore(preset,module);
                    score.getTeam().setSuffix(String.valueOf(seconds));
                }
                if(seconds <= 3 || seconds % 5 == 0)Messages.broadcastMessageTitle(ChatColor.BLUE + String.valueOf(seconds),"",new Messages.TitleTiming(5,10,2));
                for(Player p:Bukkit.getOnlinePlayers())p.playSound(p.getLocation().getBlock().getLocation(), Sound.NOTE_BASS,1f,1f);
            }

            public void end(){
                setState(State.PREGAME);
            }
        };
    }

    public void cancelWaiting(){
        if(timer == null)return;
        timer.cancel();
        timer = null;
        BoardModule module = LOBBY.getModule("Status");
        for(GameBoard board:GameBoard.getBoards()){
            BoardScore score = board.getScore(LOBBY,module);
            score.getTeam().setSuffix(playerneed);
        }
    }

    public abstract void cleanup();

    public Map<UUID, Vote> getVotes() {
        return votes;
    }

    public void resetVotes(UUID u) {
        getVotes().remove(u);
    }

    public void addVote(UUID u, GameMap vote) {
        if (getVotes().containsKey(u))
            getVotes().get(u).setVote(vote);
    }

    public void win(Player p){
        if(getState() != State.INGAME)return;
        p.playSound(p.getLocation().getBlock().getLocation(),Sound.LEVEL_UP,5F,5F);
        Messages.broadcastMessageTitle(ChatColor.BLUE + p.getName(),ChatColor.AQUA + "Has won the game",
                                       new Messages.TitleTiming(5,20,20));
        setState(State.ENDGAME);
        for(Player t:Bukkit.getOnlinePlayers()){
            if(t != p)t.teleport(p);
        }
        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public abstract void onStateChange(State oldstate, State newstate);

    public abstract Kit getDefaultKit();

    public abstract int getMaxPlayers();

    public abstract int getMinPlayers();

    public abstract int getMinTeamsize();

    public abstract int getMaxTeamsize();

    public abstract BoardPreset getScorePreset();

    public abstract GameMap loadMap(String name, MapData data, File yml, File zip);

    public abstract String getTablesuffix();

    public abstract void teleportPlayers(GameMap map, World w);

    public abstract GameMode getGameMode();

    public enum State {
        HIDDEN, LOADING, LOBBY, PREGAME, INGAME, ENDGAME, RESTARTING;

        protected static State state = null;

        public boolean joinable() {
            return this == LOBBY || this == INGAME;
        }

        public BoardPreset getPreset() {
            if (this == LOBBY)
                return BubbleGameAPI.LOBBY;
            else if (this == PREGAME || this == INGAME || this == ENDGAME)
                return BubbleGameAPI.getInstance().getScorePreset();
            return null;
        }
    }

    private class SimpleEntry<K, V> implements Map.Entry<K,V> {
        private K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(Object value) {
            this.value = (V) value;
            return (V) value;
        }
    }
}
