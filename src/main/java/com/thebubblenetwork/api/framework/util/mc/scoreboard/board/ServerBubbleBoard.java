package com.thebubblenetwork.api.framework.util.mc.scoreboard.board;

import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BubbleBoardAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacob on 14/12/2015.
 */
public class ServerBubbleBoard extends BubbleBoardAPI {
    public ServerBubbleBoard(Map<DisplaySlot, String> displaynames) {
        super("ServerBoard", displaynames);
    }

    @Override
    public Collection<Player> getPlayers() {
        List<Player> online = new ArrayList<>();
        online.addAll(Bukkit.getOnlinePlayers());
        return online;
    }
}
