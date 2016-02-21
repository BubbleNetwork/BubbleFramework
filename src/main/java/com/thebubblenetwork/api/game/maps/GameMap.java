package com.thebubblenetwork.api.game.maps;

import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import com.thebubblenetwork.api.global.data.InvalidBaseException;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.FileUTIL;
import com.thebubblenetwork.api.global.file.SSLUtil;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacob on 13/12/2015.
 */
public abstract class GameMap {

    private static List<GameMap> maps = new ArrayList<GameMap>();
    private static File folder = new File("Maps");

    private File yml, zip;
    private MapData data;
    private String name;
    private Map settings;

    public GameMap(String name, MapData data, File yml, File zip) {
        try {
            SSLUtil.allowAnySSL();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.name = name;
        this.data = data;
        this.yml = yml;
        this.zip = zip;
        settings = loadSetting(YamlConfiguration.loadConfiguration(yml).getConfigurationSection("settings"));
    }

    public static List<GameMap> getMaps() {
        return maps;
    }

    public static GameMap getMap(String s) {
        for (GameMap map : getMaps())
            if (map.getName().equalsIgnoreCase(s))
                return map;
        return null;
    }

    public static void doMaps() {
        try {
            loadMaps();
            extractMaps();
            setupMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadMaps() throws SQLException, ClassNotFoundException {
        try {
            SSLUtil.allowAnySSL();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!folder.exists())
            folder.mkdir();
        for (Map.Entry<String, MapData> data : MapData.loadData().entrySet()) {
            File downloadto = new File(folder + File.separator + data.getKey());
            File zip = new File(downloadto + ".zip");
            File yml = new File(downloadto + ".yml");
            try {
                DownloadUtil.download(zip, data.getValue().getZip());
                DownloadUtil.download(yml, data.getValue().getYaml());
            } catch (Exception e) {
                e.printStackTrace();
            }
            registerMap(BubbleGameAPI.getInstance().loadMap(data.getKey(), data.getValue(), yml, zip));
        }
    }

    public static void registerMap(GameMap map) {
        getMaps().add(map);
    }

    private static void extractMaps() {
        for (GameMap m : getMaps())
            extractMap(m);
    }

    private static void extractMap(GameMap map) {
        File mapto = new File(map.getName());
        if (mapto.exists())
            return;
        File temp = new File("temp");
        try {
            FileUTIL.unZip(map.getZip().getPath(), temp.getPath());
        } catch (IOException e) {
            //Automatic Catch Statement
            e.printStackTrace();
        }
        try {
            FileUTIL.copy(new File(temp + File.separator + "world"), mapto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUTIL.deleteDir(temp);
    }

    private static void setupMaps() {
        for (GameMap map : getMaps())
            setupMap(map);
    }

    private static World setupMap(GameMap map) {
        World world = Bukkit.getWorld(map.getName());
        if (world != null)
            return world;
        World w = new WorldCreator(map.getName()).generator(VoidWorldGenerator.getGenerator()).generateStructures
                (false).createWorld();
        w.setAutoSave(false);
        w.setDifficulty(Difficulty.NORMAL);
        w.setFullTime(0L);
        return w;
    }


    public Map getSettings() {
        return settings;
    }

    public abstract Map loadSetting(ConfigurationSection section);


    public String getName() {
        return name;
    }

    public File getYml() {
        return yml;
    }

    public File getZip() {
        return zip;
    }

    public MapData getData() {
        return data;
    }

    public String[] getDescription() {
        try {
            return getData().getString(MapData.DESCRIPTION).split(",");
        } catch (InvalidBaseException e) {
            return new String[0];
        }
    }
}
