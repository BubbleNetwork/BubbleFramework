package com.thebubblenetwork.api.framework.util.mc.chat;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 12/12/2015.
 */
public class ChatColorAppend {
    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String wipe(String s) {
        return ChatColor.stripColor(s);
    }


    //ChatColor.getLastColors(String s); Also works but I prefer mine
    public static List<ChatColor> getLastColor(String s) {
        List<ChatColor> format = new ArrayList<>();
        for (int i = s.length() - 2; i >= 0; i--) {
            if (s.charAt(i) == ChatColor.COLOR_CHAR) {
                ChatColor color = ChatColor.getByChar(s.charAt(i + 1));
                format.add(color);
                if (!color.isFormat()) {
                    return format;
                }
            }
        }
        return format;
    }

    public static String getLastColorString(String s) {
        return Joiner.on("").join(getLastColor(s));
    }
}
