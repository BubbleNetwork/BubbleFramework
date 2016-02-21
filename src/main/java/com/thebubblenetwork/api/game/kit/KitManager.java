package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jacob on 13/12/2015.
 */
public class KitManager {
    private static List<Kit> kits = new ArrayList<>();

    protected static void register(Kit k) {
        if(isKit(k.getNameClear()))throw new IllegalArgumentException("Kit already exists");
        kits.add(k);
        Collections.sort(kits, new Comparator<Kit>() {
            public int compare(Kit o1, Kit o2) {
                return o1.getPrice() - o2.getPrice();
            }
        });
    }

    public static Kit getKit(String name) {
        String wiped = ChatColorAppend.wipe(name);
        for (Kit k : kits) {
            if (k.getNameClear().equalsIgnoreCase(wiped))
                return k;
        }
        return null;
    }

    public static boolean isKit(String name) {
        return getKit(name) != null;
    }

    public static List<Kit> getKits() {
        return kits;
    }
}
