package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.commands.BubbleCommand;
import com.thebubblenetwork.api.framework.commands.CommandType;
import com.thebubblenetwork.api.framework.data.PlayerData;
import com.thebubblenetwork.api.framework.data.api.InvalidBaseException;
import com.thebubblenetwork.api.framework.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jacob on 31/12/2015.
 */
public class BubblePlayer {

    private static Map<UUID, BubblePlayer> playerMap = new HashMap<>();
    private static Listener listener = new Listener() {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerJoinEvent e) {
            Player p = e.getPlayer();
            try {
                BubblePlayer player = new BubblePlayer(PlayerData.loadData(p.getUniqueId()));
                playerMap.put(p.getUniqueId(), player);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerQuit(PlayerQuitEvent e) {
            Player p = e.getPlayer();
            playerMap.remove(p.getUniqueId());
        }
    };
    private PlayerData data;
    private Player p;


    public BubblePlayer(PlayerData data) {
        this.data = data;
    }

    public static BubblePlayer get(Player p) {
        return get(p.getUniqueId());
    }

    @Deprecated
    public static BubblePlayer get(UUID p) {
        return playerMap.get(p);
    }

    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private UUID[] getFriendsList(String indentifier) throws InvalidBaseException {
        if (!indentifier.startsWith(PlayerData.FRIENDSBASE))
            throw new InvalidBaseException(indentifier + " does not start with " + PlayerData.FRIENDSBASE);
        String[] strings = getData().getString(indentifier).split(",");
        UUID[] uuids = new UUID[strings.length];
        for (int i = 0; i < strings.length; i++) {
            uuids[i] = UUID.fromString(strings[i]);
        }
        return uuids;
    }

    private UUID[] getFriends() {
        try {
            return getFriendsList(PlayerData.FRIENDSLIST);
        } catch (InvalidBaseException e) {
            return new UUID[0];
        }
    }

    private UUID[] getPendingFriends() {
        try {
            return getFriendsList(PlayerData.FRIENDSPENDINGLIST);
        } catch (InvalidBaseException e) {
            return new UUID[0];
        }
    }

    private UUID[] getFriendsInvited() {
        try {
            return getFriendsList(PlayerData.FRIENDSINVITELIST);
        } catch (InvalidBaseException e) {
            return new UUID[0];
        }
    }

    private Map<String, Integer> getMapRaw(String indentifier) {
        Map<String, Integer> map = new HashMap<>();
        for (Object o : getData().getRaw().keySet()) {
            if (o instanceof String) {
                String s = (String) o;
                if (s.startsWith(indentifier)) {
                    int i;
                    try {
                        i = getData().getNumber(s).intValue();
                    } catch (InvalidBaseException ex) {
                        continue;
                    }
                    map.put(s.replace(indentifier + ".", ""), i);
                }
            }
        }
        return map;
    }

    private Map<String, Integer> getMap(String id, String uid) {
        String indentifier = id + "." + uid.toLowerCase();
        return getMapRaw(indentifier);
    }

    public Map<String, Integer> getStats(String game) {
        return getMap(PlayerData.STATSBASE, game);
    }

    public Map<String, Integer> getKits(String game) {
        return getMap(PlayerData.KITBASE, game);
    }

    public Map<String, Integer> getHubItems() {
        return getMapRaw(PlayerData.ITEMSBASE);
    }

    private Map<String, Integer> getCurrency() {
        return getMapRaw(PlayerData.CURRENCYBASE);
    }

    public int getTokens() {
        Map<String, Integer> currency = getCurrency();
        return currency.containsKey(PlayerData.TOKENS) ? currency.get(PlayerData.TOKENS) : 0;
    }

    private String getRankString() throws InvalidBaseException {
        return getData().getString(PlayerData.MAINRANK);
    }

    public Rank getRank() {
        String s;
        try {
            s = getRankString();
        } catch (InvalidBaseException e) {
            BubbleNetwork.getInstance().getLogger().severe("Rank Data not found for " + getPlayer().getName() + e
                    .getMessage());
            return Rank.getDefault();
        }
        return Rank.getRank(s);
    }

    private String[] getSubRanksString() throws InvalidBaseException {
        return getData().getString(PlayerData.SUBRANKS).split(",");
    }

    private Rank[] getSubRanks() {
        String[] s;
        try {
            s = getSubRanksString();
        } catch (InvalidBaseException e) {
            return new Rank[0];
        }
        List<Rank> ranks = new ArrayList<>();
        for (String rankname : s) {
            Rank r = Rank.getRank(rankname);
            if (r != null)
                ranks.add(r);
        }
        return ranks.toArray(new Rank[0]);
    }

    public Player getPlayer() {
        if (p == null)
            p = Bukkit.getPlayer(getData().getUUID());
        return p;
    }

    public PlayerData getData() {
        return data;
    }

    public String getNick() {
        try {
            return getData().getString("nick");
        } catch (InvalidBaseException ex) {
            return getPlayer().getName();
        }
    }

    public boolean isAuthorized(String permissionraw, CommandType type) {
        if (BubbleCommand.isAuthorized(getRank(), permissionraw, type))
            return true;
        for (Rank r : getSubRanks())
            if (BubbleCommand.isAuthorized(r, permissionraw, type))
                return true;
        return false;
    }

    public boolean isAuthorized(BubbleCommand command) {
        return isAuthorized(command.getPermission(), command.getType());
    }
}
