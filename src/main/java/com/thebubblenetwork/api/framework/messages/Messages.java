package com.thebubblenetwork.api.framework.messages;

import com.sun.istack.internal.Nullable;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.titlemanager.TitleMessanger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;

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
        try {
            TitleMessanger.sendActionBar(p, message);
        } catch (Throwable ex) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not send actionbar", ex);
        }
    }

    public static void broadcastMessageTitle(String title, String subtitle, TitleTiming timing) {
        sendMessageTitle(Bukkit.getOnlinePlayers(), title, subtitle, timing);
    }

    public static void sendMessageTitle(Player[] ps, String title, String subtitle, TitleTiming timing) {
        sendMessageTitle(Arrays.asList(ps), title, subtitle, timing);
    }

    public static void sendMessageTitle(Iterable<? extends Player> ps, String title, String subtitle, TitleTiming timing) {
        for (Player p : ps) {
            sendMessageTitle(p, title, subtitle, timing);
        }
    }

    public static void sendMessageTitle(Player p, String title, @Nullable String subtitle, TitleTiming timing) {
        try {
            TitleMessanger.sendTitle(p, title, TitleMessanger.ProtcolInjectorReflection.TITLE, timing.getIn(), timing.getShow(), timing.getOut());
            if (subtitle != null) {
                TitleMessanger.sendTitleNoTiming(p, subtitle, TitleMessanger.ProtcolInjectorReflection.SUBTITLE);
            }
        } catch (Throwable throwable) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not send title/subtitle",throwable);
        }
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
