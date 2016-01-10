package com.thebubblenetwork.api.framework.ranks;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.data.RankData;
import com.thebubblenetwork.api.framework.data.api.InvalidBaseException;
import com.thebubblenetwork.api.framework.util.sql.SQLUtil;
import net.minecraft.util.com.google.common.collect.Iterables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob on 31/12/2015.
 */
public class Rank {

    public static String table = "ranks";
    private static Map<String, Rank> ranks = new HashMap<>();
    private RankData data;
    private String name;

    public Rank(String name, RankData data) {
        this.name = name;
        this.data = data;
    }

    public static Rank getDefault() {
        for (Rank r : ranks.values())
            if (r.isDefault())
                return r;
        Rank r;
        return (r = ranks.get("default")) != null ? r : ranks.size() > 0 ? Iterables.get(ranks.values(), 0) : null;
    }

    public static Rank getRank(String s) {
        return ranks.get(s);
    }

    public static void loadRanks() throws SQLException, ClassNotFoundException {
        ranks.clear();
        ResultSet set = SQLUtil.query(BubbleNetwork.getInstance().getConnection(), table, "*", new SQLUtil.Where("1"));
        Map<String, Map> map = new HashMap<>();
        while (set.next()) {
            String rankname = set.getString("rank");
            Map currentmap = map.containsKey(rankname) ? map.get(rankname) : new HashMap();
            currentmap.put(set.getObject("key"), set.getObject("value"));
            map.put(rankname, currentmap);
        }
        set.close();
        for (Map.Entry<String, Map> entry : map.entrySet()) {
            ranks.put(entry.getKey(), new Rank(entry.getKey(), new RankData(entry.getKey(), entry.getValue())));
        }
    }

    private static boolean isAuthorized(Rank r, String indentifier) {
        boolean b = false;
        try {
            b = r.getData().getBoolean(indentifier);
        } catch (InvalidBaseException e) {
        }
        return b || (r.getInheritance() != null && isAuthorized(r.getInheritance(), indentifier));
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        try {
            return getData().getString(RankData.PREFIX);
        } catch (InvalidBaseException ex) {
            return "";
        }
    }

    public String getSuffix() {
        try {
            return getData().getString(RankData.SUFFIX);
        } catch (InvalidBaseException ex) {
            return "";
        }
    }

    protected String getInheritanceString() {
        try {
            return getData().getString(RankData.INHERITANCE);
        } catch (InvalidBaseException ex) {
            return null;
        }
    }

    public boolean isDefault() {
        try {
            return getData().getBoolean("default");
        } catch (InvalidBaseException e) {
            return false;
        }
    }

    public Rank getInheritance() {
        return ranks.get(getInheritanceString());
    }

    public boolean isAuthorized(String indentifier) {
        return isAuthorized(this, indentifier);
    }

    public RankData getData() {
        return data;
    }
}
