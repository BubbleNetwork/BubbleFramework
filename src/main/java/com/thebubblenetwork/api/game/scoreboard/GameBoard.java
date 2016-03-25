package com.thebubblenetwork.api.game.scoreboard;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.board.SingleBubbleBoard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;

/**
 * Created by Jacob on 14/12/2015.
 */
public class GameBoard extends SingleBubbleBoard {
    public static Collection<GameBoard> getBoards() {
        return boardMap.values();
    }

    public static void setBoard(Player p,GameBoard board){
        boardMap.put(p.getUniqueId(),board);
    }

    public static void removeBoard(Player p){
        boardMap.remove(p.getUniqueId());
    }

    public static GameBoard getBoard(Player p) {
        return boardMap.get(p.getUniqueId());
    }

    private static Map<DisplaySlot, String> displayname = Collections.singletonMap(DisplaySlot.SIDEBAR, BubbleNetwork.getPrefix());
    //CUSTOM LOGGING
    private static Map<UUID, GameBoard> boardMap = new HashMap<>();

    public GameBoard(Player p) {
        super(p, displayname);
    }
}
