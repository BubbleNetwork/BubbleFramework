package com.thebubblenetwork.api.framework.util.mc.scoreboard.util;

import com.thebubblenetwork.api.framework.util.mc.scoreboard.api.BoardModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Created by Administrator on 22/12/2015.
 */
public class BoardModuleBuilder {
    private String name;
    private String display = "";
    private DisplaySlot slot = DisplaySlot.SIDEBAR;
    private int score;

    public BoardModuleBuilder(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public BoardModuleBuilder withDefaultScore(int score) {
        this.score = score;
        return this;
    }

    public BoardModuleBuilder withSlot(DisplaySlot slot) {
        this.slot = slot;
        return this;
    }

    public BoardModuleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public BoardModuleBuilder withDisplay(String display) {
        this.display = display;
        return this;
    }

    public BoardModuleBuilder withRandomDisplay() {
        this.display = BoardModule.getCombo().getRandomColorCombo() + ChatColor.RESET;
        return this;
    }

    public BoardModule build() {
        return new BoardModule(name, display, score, slot);
    }
}
