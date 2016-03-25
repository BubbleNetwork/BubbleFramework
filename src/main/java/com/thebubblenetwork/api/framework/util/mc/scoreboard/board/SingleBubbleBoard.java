package com.thebubblenetwork.api.framework.util.mc.scoreboard.board;

import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BubbleBoardAPI;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Jacob on 14/12/2015.
 */
public class SingleBubbleBoard extends BubbleBoardAPI {
    private Player p;


    public SingleBubbleBoard(Player p, Map<DisplaySlot, String> displaynames) {
        super(p.getName(), displaynames);
        this.p = p;
    }

    @Override
    public Collection<Player> getPlayers() {
        return Collections.singletonList(p);
    }
}
