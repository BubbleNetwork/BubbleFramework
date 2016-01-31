package com.thebubblenetwork.api.game.scoreboard;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.BoardPreset;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.SingleBubbleBoard;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;

/**
 * Created by Jacob on 14/12/2015.
 */
public class GameBoard extends SingleBubbleBoard {
    private static Map<DisplaySlot, String> displayname = Collections.singletonMap(DisplaySlot.SIDEBAR, BubbleNetwork
            .getPrefix());
    //CUSTOM LOGGING
    private static Map<UUID, GameBoard> boardMap = new HashMap<>();

    public GameBoard(Player p) {
        super(p, displayname);
    }

    public static Collection<GameBoard> getBoards() {
        return boardMap.values();
    }

    public static GameBoard getBoard(Player p) {
        return boardMap.get(p.getUniqueId());
    }

    public static void registerlistener(BubblePlugin api) {
        api.registerListener(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                BubblePlayer<Player> player = BukkitBubblePlayer.getObject(e.getPlayer().getUniqueId());
                GameBoard board = new GameBoard(player.getPlayer());
                boardMap.put(player.getPlayer().getUniqueId(), board);
                BoardPreset preset = BubbleGameAPI.getInstance().getState().getPreset();
                if (preset != null) {
                    board.enable(preset);
                }
                player.getPlayer().setScoreboard(board.getObject().getBoard());
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                boardMap.remove(e.getPlayer().getUniqueId());
            }
        });
    }
}
