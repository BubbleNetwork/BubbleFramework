package com.thebubblenetwork.api.framework.messages;

import com.sun.istack.internal.Nullable;
import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.titlemanager.ActionType;
import com.thebubblenetwork.api.framework.messages.titlemanager.NMSTitles;
import com.thebubblenetwork.api.framework.messages.titlemanager.types.SubtitleTitle;
import com.thebubblenetwork.api.framework.messages.titlemanager.types.TimingTicks;
import com.thebubblenetwork.api.framework.messages.titlemanager.types.TimingTitle;
import com.thebubblenetwork.api.framework.messages.titlemanager.types.TitleTitle;
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
        NMSTitles.sendChat(p, ActionType.ACTION, message);
    }

    public static void broadcastMessageTitle(@Nullable String title,@Nullable String subtitle,@Nullable TimingTicks timing) {
        sendMessageTitle(Bukkit.getOnlinePlayers(), title, subtitle, timing);
    }

    public static void sendMessageTitle(Player[] ps, String title,@Nullable String subtitle,@Nullable TimingTicks timing) {
        sendMessageTitle(Arrays.asList(ps), title, subtitle, timing);
    }

    public static void sendMessageTitle(Iterable<? extends Player> ps,@Nullable String title,@Nullable String subtitle,@Nullable TimingTicks timing) {
        for (Player p : ps) {
            sendMessageTitle(p, title, subtitle, timing);
        }
    }

    public static void sendMessageTitle(Player p,@Nullable String title, @Nullable String subtitle,@Nullable TimingTicks timing) {
        try {
            if(title != null) {
                NMSTitles.sendTitle(p, new TitleTitle(title));
            }
            if (subtitle != null) {
                NMSTitles.sendTitle(p,new SubtitleTitle(subtitle));
            }
            if(timing != null){
                NMSTitles.sendTitle(p,new TimingTitle(timing));
            }
        } catch (Throwable throwable) {
            BubbleNetwork.getInstance().getLogger().log(Level.WARNING, "Could not send title/subtitle",throwable);
        }
    }


}
