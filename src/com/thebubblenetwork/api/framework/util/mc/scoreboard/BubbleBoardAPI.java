package com.thebubblenetwork.api.framework.util.mc.scoreboard;

import com.thebubblenetwork.api.framework.util.mc.chat.RandomColorCombo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Jacob on 14/12/2015.
 */
public abstract class BubbleBoardAPI {
    private static ScoreboardManager manager;
    private RandomColorCombo colorCombo = new RandomColorCombo(3);
    private ScoreboardObject object;
    private String name;
    private Map<BoardPreset, Map<BoardModule, BoardScore>> modules = new HashMap<>();
    private static Collection<DisplaySlot> getObjectives(){
        List<DisplaySlot> displaySlots = new ArrayList<>();
        for(BoardPreset preset:BoardPreset.getPresetlist()){
            for(BoardModule module:preset.getPresets()){
                if(!displaySlots.contains(module.getSlot()))displaySlots.add(module.getSlot());
            }
        }
        return displaySlots;
    }

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

    public static ScoreboardManager getManager() {
        if (manager == null)
            manager = Bukkit.getScoreboardManager();
        return manager;
    }


    public Map<BoardPreset, Map<BoardModule, BoardScore>> getModules() {
        return modules;
    }

    public void enable(BoardPreset preset) {
        List<ObjectiveUpdate> updateList = new ArrayList<>();
        for (Map.Entry<BoardPreset, Map<BoardModule, BoardScore>> presetEntry : getModules().entrySet()) {
            boolean b = presetEntry.getKey() == preset;
            for (final BoardScore score : presetEntry.getValue().values()) {
                if (b)
                    updateList.add(new ObjectiveUpdate(score.getModule().getSlot()) {
                        @Override
                        public void update(Objective o) {
                            score.setScoreManual(o);
                        }
                    });
                else
                    score.unsetScore();
            }
        }
        getObject().update(updateList);
        preset.onEnable(this);
    }

    public BoardScore getScore(@Nullable BoardPreset preset, BoardModule module) {
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
