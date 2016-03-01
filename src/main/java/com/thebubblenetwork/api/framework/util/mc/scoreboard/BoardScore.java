package com.thebubblenetwork.api.framework.util.mc.scoreboard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Map;

/**
 * Created by Jacob on 14/12/2015.
 */
public class BoardScore {
    private BoardModule module;
    private BubbleBoardAPI api;
    private Team team;
    private boolean set = false;

    public BoardScore(BoardModule module, BubbleBoardAPI api) {
        this.module = module;
        this.api = api;
        String name = module.getName();
        if (name.length() > 16) {
            name = name.substring(16);
        }
        team = api.getObject().getBoard().getTeam(name);
        if (team == null) {
            team = api.getObject().getBoard().registerNewTeam(name);
        } else {
            if (name.length() > 6) {
                name = name.substring(6);
            }
            team = api.getObject().getBoard().registerNewTeam(name + api.getColorCombo().getRandomColorCombo() +
                    ChatColor.RESET);
        }
        team.addPlayer(module.getPlayer());
    }

    public void unsetScore() {
        getApi().getObject().getBoard().resetScores(getModule().getPlayer());
        set = false;
    }

    public void delete() {
        unsetScore();
        getTeam().unregister();
        for (Map<BoardModule, BoardScore> map : getApi().getModules().values()) {
            if (map.containsKey(getModule()) && map.containsValue(this)) {
                map.put(getModule(), new BoardScore(getModule(), api));
            }
        }
        api = null;
        module = null;
        team = null;
    }

    protected void setScoreManual(Objective o, int score) {
        o.getScore(getModule().getPlayer()).setScore(score);
        set = true;
    }

    protected void setScoreManual(Objective o) {
        setScoreManual(o, getModule().getDefaultscore());
    }

    public void setScore(final int score) {
        getApi().getObject().update(new ObjectiveUpdate(getModule().getSlot()) {
            @Override
            public void update(Objective o) {
                setScoreManual(o, score);
            }
        });
    }

    public void setScore() {
        setScore(getModule().getDefaultscore());
    }

    public BubbleBoardAPI getApi() {
        return api;
    }

    public Team getTeam() {
        return team;
    }

    public BoardModule getModule() {
        return module;
    }
}
