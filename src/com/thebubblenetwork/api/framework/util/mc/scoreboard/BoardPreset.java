package com.thebubblenetwork.api.framework.util.mc.scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 15/12/2015.
 */
public abstract class BoardPreset {
    private static List<BoardPreset> presetlist = new ArrayList<>();
    private String name;
    private BoardModule[] presets;

    public BoardPreset(String name, BoardModule... presets) {
        this.name = name;
        this.presets = presets;
        presetlist.add(this);
    }

    public static BoardPreset getPreset(String s) {
        for (BoardPreset preset : getPresetlist())
            if (preset.getName().equalsIgnoreCase(s))
                return preset;
        return null;
    }

    public static List<BoardPreset> getPresetlist() {
        return presetlist;
    }

    public BoardModule getModule(String s) {
        for (BoardModule module : presets) {
            if (module.getName().equals(s))
                return module;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public BoardModule[] getPresets() {
        return presets;
    }

    public abstract void onEnable(BubbleBoardAPI api);
}
