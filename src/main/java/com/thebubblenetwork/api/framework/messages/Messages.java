package com.thebubblenetwork.api.framework.messages;

import com.thebubblenetwork.api.framework.messages.bossbar.BubbleBarAPI;
import com.thebubblenetwork.api.framework.messages.titlemanager.TitleMessanger;
import com.thebubblenetwork.api.framework.util.version.VersionUTIL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Jacob on 09/12/2015.
 */
public class Messages {

    public static void broadcastMessageAction(String message) {
        sendMessageAction(Bukkit.getOnlinePlayers(), message);
    }

    public static void sendMessageAction(Player[] ps, String message) {
        sendMessageAction(Arrays.asList(ps), message);
    }

    public static void sendMessageAction(Iterable<? extends Player> ps, String message) {
        for (Player p : ps) {
            sendMessageAction(p, message);
        }
    }

    public static void sendMessageAction(Player p, String message) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V17) {
            setBar(p, message);
        } else {
            try {
                TitleMessanger.sendActionBar(p, message);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void broadcastMessageTitle(String title, String subtitle, TitleTiming timing) {
        sendMessageTitle(Bukkit.getOnlinePlayers(), title, subtitle, timing);
    }

    public static void sendMessageTitle(Player[] ps, String title, String subtitle, TitleTiming timing) {
        sendMessageTitle(Arrays.asList(ps), title, subtitle, timing);
    }

    public static void sendMessageTitle(
            Iterable<? extends Player> ps, String title, String subtitle, TitleTiming timing) {
        for (Player p : ps) {
            sendMessageTitle(p, title, subtitle, timing);
        }
    }

    public static void sendMessageTitle(Player p, String title, String subtitle, TitleTiming timing) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V17) {
            setBar(p, title + "  " + subtitle);
        } else {
            try {
                sendTitleAndSubtitle(p, title, subtitle, timing);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    @Deprecated
    public static void sendTitleAndSubtitle(Player p, String title, String subtitle, TitleTiming timing) {
        try {
            TitleMessanger.sendTitle(p, TitleMessanger.ProtcolInjectorReflection.TITLE, title);
            TitleMessanger.sendTitle(p, TitleMessanger.ProtcolInjectorReflection.SUBTITLE, subtitle);
            TitleMessanger.sendTitle(p, TitleMessanger.ProtcolInjectorReflection.TIMES, timing.getIn(), timing.getShow(),
                    timing.getOut());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void setBar(Player p, String message) {
        BubbleBarAPI.setBarDragonTimer(p, message, 2);
    }

    public static class TitleTiming {
        private int in, show, out;

        public TitleTiming(int in, int show, int out) {
            this.in = in;
            this.show = show;
            this.out = out;
        }


        public int getIn() {
            return in;
        }

        public void setIn(int in) {
            this.in = in;
        }

        public int getShow() {
            return show;
        }

        public void setShow(int show) {
            this.show = show;
        }

        public int getOut() {
            return out;
        }

        public void setOut(int out) {
            this.out = out;
        }
    }


}