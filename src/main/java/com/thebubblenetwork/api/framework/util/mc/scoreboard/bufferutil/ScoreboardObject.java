package com.thebubblenetwork.api.framework.util.mc.scoreboard.bufferutil;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Jacob on 13/12/2015.
 */
public abstract class ScoreboardObject implements Callable<Object> {
    private Scoreboard board;
    private Map<DisplaySlot, Objective> buffer, objective;

    public ScoreboardObject() {
        board = createScoreboard();
        objective = createObjectives(board);
        buffer = createObjectives(board);
    }

    private void swapBuffer() {
        for (Map.Entry<DisplaySlot, Objective> entry : buffer.entrySet()) {
            entry.getValue().setDisplaySlot(entry.getKey());
        }
        Map<DisplaySlot, Objective> tempObjective = buffer;
        buffer = objective;
        objective = tempObjective;
    }

    private void updateBuffer(Iterable<ObjectiveUpdate> updates) {
        for (ObjectiveUpdate update : updates) {
            updateBuffer(update);
        }
    }

    private void updateBuffer(ObjectiveUpdate update) {
        update.update(buffer.get(update.getSlot()));
    }

    public void update(ObjectiveUpdate update) {
        updateBuffer(update);
        swapBuffer();
        updateBuffer(update);
    }

    public void update(Iterable<ObjectiveUpdate> updates) {
        updateBuffer(updates);
        swapBuffer();
        updateBuffer(updates);
    }

    public Scoreboard getBoard() {
        return board;
    }

    public abstract Map<DisplaySlot, Objective> createObjectives(Scoreboard board);

    public abstract Scoreboard createScoreboard();
}
