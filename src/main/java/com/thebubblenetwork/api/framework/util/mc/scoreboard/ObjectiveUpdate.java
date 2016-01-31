package com.thebubblenetwork.api.framework.util.mc.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Created by Jacob on 13/12/2015.
 */
public abstract class ObjectiveUpdate {
    private DisplaySlot slot;

    public ObjectiveUpdate(DisplaySlot slot) {
        this.slot = slot;
    }

    public DisplaySlot getSlot() {
        return slot;
    }

    public abstract void update(Objective o);
}
