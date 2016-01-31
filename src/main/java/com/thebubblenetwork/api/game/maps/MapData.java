package com.thebubblenetwork.api.game.maps;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.data.DataObject;
import com.thebubblenetwork.api.global.data.InvalidBaseException;
import com.thebubblenetwork.api.global.sql.SQLUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 03/01/2016.
 */
public class MapData extends DataObject {
    public static String DOWNLOADBASE = "download", DOWNLOADZIP = DOWNLOADBASE + ".zip", DOWNLOADYML = DOWNLOADBASE +
            ".yml", DESCRIPTION = "description";
    public static String maptable = "maps";

    public MapData(Map data) {
        super(data);
    }

    public static Set<Map.Entry<String, MapData>> loadData() throws ClassNotFoundException, SQLException {
        Map<String, Map> datamap = new HashMap<>();
        ResultSet set = SQLUtil.query(BubbleNetwork.getInstance().getConnection(), maptable, "*", new SQLUtil.Where
                ("1"));
        while (set.next()) {
            String mapname = set.getString("map");
            Map currentmap = datamap.containsKey(mapname) ? datamap.get(mapname) : new HashMap();
            currentmap.put(set.getObject("key"), set.getObject("value"));
            datamap.put(mapname, currentmap);
        }
        set.close();
        Map<String, MapData> map = new HashMap<>();
        for (Map.Entry<String, Map> data : datamap.entrySet()) {
            map.put(data.getKey(), new MapData(data.getValue()));
        }
        return map.entrySet();
    }

    public String getYaml() {
        try {
            return getString(DOWNLOADYML);
        } catch (InvalidBaseException e) {
        }
        return null;
    }

    public String getZip() {
        try {
            return getString(DOWNLOADZIP);
        } catch (InvalidBaseException e) {
        }
        return null;
    }
}
