package com.thebubblenetwork.api.framework.util.mc.scoreboard;

import com.thebubblenetwork.api.framework.util.mc.chat.RandomColorCombo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Created by Jacob on 14/12/2015.
 */
public class BoardModule implements Cloneable {
    public static RandomColorCombo getCombo() {
        return combo;
    }

    private static RandomColorCombo combo = new RandomColorCombo(4);
    private String name;
    private String display;
    private OfflinePlayer player;
    private DisplaySlot slot;
    private int defaultscore;

    public BoardModule(String name, String display, int defaultscore, DisplaySlot slot) {
        this.name = name;
        this.display = display;
        this.slot = slot;
        this.defaultscore = defaultscore;
    }

    public BoardModule(String name, int defaultscore) {
        this(name, combo.getRandomColorCombo(), defaultscore, DisplaySlot.SIDEBAR);
    }

    public OfflinePlayer getPlayer() {
        if (player == null) {
            player = Bukkit.getOfflinePlayer(display);
        }
        return player;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public DisplaySlot getSlot() {
        return slot;
    }

    public BoardModule clone() {
        return new BoardModule(name, display, defaultscore, slot);
    }

    public int getDefaultscore() {
        return defaultscore;
    }

    public void setDefaultscore(int defaultscore) {
        this.defaultscore = defaultscore;
    }
}
