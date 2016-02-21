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

    public MapData(Map<String,String> data) {
        super(data);
    }

    public static Map<String,MapData> loadData() throws ClassNotFoundException, SQLException {
        ResultSet set = null;
        try {
            set = SQLUtil.query(BubbleNetwork.getInstance().getConnection(), maptable, "*", new SQLUtil.Where
                    ("1"));
            Map<String,MapData> objectMap = new HashMap<>();
            while(set.next()){
                String title = set.getString("map");
                String key = set.getString("key");
                String value = set.getString("value");
                MapData data = objectMap.containsKey(title) ? objectMap.get(title) :  new MapData(new HashMap<>());
                data.getRaw().put(key,value);
                objectMap.put(title,data);
            }
            return objectMap;
        }
        finally {
            if(set != null){
                try{
                    set.close();
                }
                catch (Exception ex){
                }
            }
        }
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
