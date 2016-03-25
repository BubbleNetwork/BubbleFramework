package com.thebubblenetwork.api.framework.util.mc.scoreboard.api;

import com.thebubblenetwork.api.framework.util.mc.chat.RandomColorCombo;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.bufferutil.ObjectiveUpdate;
import com.thebubblenetwork.api.framework.util.mc.scoreboard.bufferutil.ScoreboardObject;
import com.thebubblenetwork.api.global.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

/**
 * Created by Jacob on 14/12/2015.
 */
public abstract class BubbleBoardAPI {
    private static Collection<DisplaySlot> getObjectives() {
        List<DisplaySlot> displaySlots = new ArrayList<>();
        for (BoardPreset preset : BoardPreset.getPresetlist()) {
            for (BoardModule module : preset.getPresets()) {
                if (!displaySlots.contains(module.getSlot())) {
                    displaySlots.add(module.getSlot());
                }
            }
        }
        return displaySlots;
    }

    public static ScoreboardManager getManager() {
        if (manager == null) {
            manager = Bukkit.getScoreboardManager();
        }
        return manager;
    }

    private static ScoreboardManager manager;
    private RandomColorCombo colorCombo = new RandomColorCombo(3);
    private ScoreboardObject object;
    private String name;
    private Map<String,Team> rankteams = new HashMap<>();
    private Map<BoardPreset, Map<BoardModule, BoardScore>> modules = new HashMap<>();
    private BoardPreset currentpreset = null;

    public BubbleBoardAPI(final String name, Map<DisplaySlot, String> displaynames) {
        this.name = name;
        object = new ScoreboardObject() {
            public Map<DisplaySlot, Objective> createObjectives(Scoreboard board) {
                Map<DisplaySlot, Objective> map = new HashMap<>();
                for (DisplaySlot slot : getObjectives()) {
                    Objective o = board.registerNewObjective(colorCombo.getRandomColorCombo(), "dummy");
                    map.put(slot, o);
                }
                return map;
            }

            public Object call(){
                return BubbleBoardAPI.this;
            }

            public Scoreboard createScoreboard() {
                return getManager().getNewScoreboard();
            }
        };
        for (final Map.Entry<DisplaySlot, String> stringEntry : displaynames.entrySet()) {
            object.update(new ObjectiveUpdate(stringEntry.getKey()) {
                @Override
                public void update(Objective o) {
                    o.setDisplayName(stringEntry.getValue());
                }
            });
        }
        for (BoardPreset preset : BoardPreset.getPresetlist()) {
            Map<BoardModule, BoardScore> scoreMap = new HashMap<>();
            for (BoardModule module : preset.getPresets()) {
                scoreMap.put(module, new BoardScore(module, this));
            }
            modules.put(preset, scoreMap);
        }
    }

    private Team setupTeam(Rank r){
        Team t = getObject().getBoard().getTeam(r.getName());
        if(t == null)t = getObject().getBoard().registerNewTeam(r.getName());
        t.setPrefix(r.getPrefix() + " ");
        rankteams.put(r.getName(),t);
        return t;
    }

    public void applyRank(Rank r,Player p){
        Team t = rankteams.containsKey(r.getName()) ? rankteams.get(r.getName()) : setupTeam(r);
        t.addPlayer(p);
    }

    public void removeRank(Rank r,Player p){
        if(rankteams.containsKey(r.getName())){
            rankteams.get(r.getName()).removePlayer(p);
        }
    }

    public Map<BoardPreset, Map<BoardModule, BoardScore>> getModules() {
        return modules;
    }

    public void enable(BoardPreset preset) {
        currentpreset = preset;
        List<ObjectiveUpdate> updateList = new ArrayList<>();
        for (Map.Entry<BoardPreset, Map<BoardModule, BoardScore>> presetEntry : getModules().entrySet()) {
            boolean b = presetEntry.getKey() == preset;
            for (final BoardScore score : presetEntry.getValue().values()) {
                if (b) {
                    updateList.add(new ObjectiveUpdate(score.getModule().getSlot()) {
                        @Override
                        public void update(Objective o) {
                            score.setScoreManual(o);
                        }
                    });
                } else {
                    score.unsetScore();
                }
            }
        }
        getObject().update(updateList);
        preset.onEnable(this);
    }

    public BoardPreset getCurrentpreset() {
        return currentpreset;
    }

    public BoardScore getScore(BoardPreset preset, BoardModule module) {
        return getModules().get(preset).get(module);
    }

    public RandomColorCombo getColorCombo() {
        return colorCombo;
    }

    public ScoreboardObject getObject() {
        return object;
    }


    public String getName() {
        return name;
    }

    public abstract Collection<Player> getPlayers();
}
