package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.data.PlayerData;
import com.thebubblenetwork.api.framework.messages.bossbar.BubbleBarAPI;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import com.thebubblenetwork.api.framework.plugin.BubblePluginLoader;
import com.thebubblenetwork.api.framework.plugin.PluginDescriptionFile;
import com.thebubblenetwork.api.framework.ranks.Rank;
import com.thebubblenetwork.api.framework.util.files.PropertiesFile;
import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.framework.util.sql.SQLConnection;
import com.thebubblenetwork.api.framework.util.sql.SQLUtil;
import com.thebubblenetwork.api.framework.util.version.VersionUTIL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jacob on 09/12/2015.
 */

public class BubbleNetwork extends JavaPlugin {
    protected static List<BubblePlugin> pluginList = new ArrayList<>();
    private static BubbleNetwork instance;
    private static Random random = new Random();
    private static final File PROPERTIES = new File("bubblenetwork.properties");
    private static String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "BubbleNetwork" +
            ChatColor.BLUE + "] " + ChatColor.GRAY;
    private static String SQLHOST ,SQLPORT ,SQLNAME ,SQLPASS ,SQLDB;
    private static String chatFormat = "{prefix}{name}{suffix}{message}";
    private BubbleBarAPI api;
    private VersionUTIL util;
    private MenuManager manager = new MenuManager();
    private SQLConnection connection;
    private PropertiesFile file;

    public BubbleNetwork() {
        super();
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        Map<PluginDescriptionFile, File> loaderList = new HashMap<>();
        List<PluginDescriptionFile> files = new ArrayList<>();
        for (File f : getDataFolder().listFiles()) {
            if (f.getName().endsWith(".jar")) {
                try {
                    PluginDescriptionFile file = BubblePluginLoader.getPluginDescription(f);
                    loaderList.put(file, f);
                    files.add(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        Collections.sort(files, new Comparator<PluginDescriptionFile>() {
            @Override
            public int compare(PluginDescriptionFile o1, PluginDescriptionFile o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        for (PluginDescriptionFile file : files) {
            try {
                BubblePluginLoader loader = new BubblePluginLoader(loaderList.get(file), file);
                BubblePlugin plugin = loader.getPlugin();
                plugin.__init__(loader);
                register(plugin);
                getLogger().info("Loaded " + plugin.getName());
            } catch (Exception e) {
                getLogger().severe("Error loading " + file.getName() + "\n");
                e.printStackTrace();
            }
        }
    }

    public static String getChatFormat() {
        return chatFormat;
    }

    public static void setChatFormat(String chatFormat) {
        BubbleNetwork.chatFormat = chatFormat;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static Random getRandom() {
        return random;
    }

    public static List<BubblePlugin> getPluginList() {
        return pluginList;
    }

    public static BubbleNetwork getInstance() {
        return instance;
    }

    public static void register(BubblePlugin plugin) {
        pluginList.add(plugin);
    }

    public VoidWorldGenerator getDefaultWorldGenerator(String worldName, String id) {
        return VoidWorldGenerator.getGenerator();
    }

    public void startConnection() throws SQLException, ClassNotFoundException {
        connection.openConnection();
    }

    public void restartConnection() throws SQLException, ClassNotFoundException {
        connection.closeConnection();
        connection.openConnection();
    }

    public void closeConnection() throws SQLException {
        if (connection != null)
            connection.closeConnection();
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public BubbleBarAPI getBossApi() {
        return api;
    }

    public VersionUTIL getVersionUtil() {
        return util;
    }

    public void onEnable() {
        instance = this;
        api = new BubbleBarAPI();
        util = new VersionUTIL();
        try {
            file = new PropertiesFile(PROPERTIES);
            SQLHOST = file.getString("database-ip");
            SQLPORT = file.getString("database-port");
            SQLDB = file.getString("database-name");
            SQLNAME = file.getString("database-user");
            SQLPASS = file.getString("database-password");
        } catch (Exception e) {
            //Automatic Catch Statement
            e.printStackTrace();
            getLogger().severe("Could not load properties file, Plugin is disabling");
            try {
                PropertiesFile.generateFresh(PROPERTIES,
                                             new String[]{"database-ip","database-port","database-name","database-user","database-password"},
                                             new String[]{"localhost","3306","bubble","root",null});
            } catch (Exception e1) {
                //Automatic Catch Statement
                e1.printStackTrace();
            } getServer().getPluginManager().disablePlugin(this);
            return;
        }
        connection = new SQLConnection(SQLHOST, SQLPORT, SQLDB, SQLNAME, SQLPASS.equals("null") ? null : SQLPASS);;
        try {
            startConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            getLogger().severe("Could not load SQL, Plugin is disabling");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            if (!SQLUtil.tableExists(getConnection(), PlayerData.table)) {
                Map<String, Map.Entry<SQLUtil.SQLDataType, Integer>> map = new HashMap<>();
                map.put("uuid", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, 36));
                map.put("key", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                map.put("value", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                SQLUtil.createTable(getConnection(), PlayerData.table, map);
            }

            if (!SQLUtil.tableExists(getConnection(), Rank.table)) {
                Map<String, Map.Entry<SQLUtil.SQLDataType, Integer>> map = new HashMap<>();
                map.put("rank", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                map.put("key", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                map.put("value", new SimpleEntry<SQLUtil.SQLDataType, Integer>(SQLUtil.SQLDataType.TEXT, -1));
                SQLUtil.createTable(getConnection(), Rank.table, map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        EnchantGlow.getGlow();
        registerListener(api);
        api.DragonBarTask();
        registerListener(util);
        manager.register(this);
        for (BubblePlugin plugin : pluginList) {
            plugin.onEnable();
        }
        try {
            Rank.loadRanks();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        BubblePlayer.register(this);
        registerListener(new Listener() {
            private Map<AsyncPlayerChatEvent, String> map = new HashMap<AsyncPlayerChatEvent, String>();

            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerChatAsyncLow(AsyncPlayerChatEvent e) {
                map.put(e, e.getMessage());
                e.setMessage(getChatFormat());
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onPayerChatAsyncMessage(AsyncPlayerChatEvent e) {
                String msg = e.getMessage();
                e.setCancelled(true);
                msg = msg.replace("{message}", getMessage(e));
                Bukkit.broadcastMessage(msg);
                map.remove(e);
            }


            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent e) {
                BubblePlayer player = BubblePlayer.get(e.getPlayer());
                Rank r;
                String format = e.getMessage();
                format = format.replace("{name}", e.getPlayer().getName());
                if ((r = player.getRank()) != null) {
                    format = format.replace("{prefix}", ChatColorAppend.translate(r.getPrefix())).replace("{suffix}",
                                                                                                          ChatColorAppend.translate(r.getSuffix()));
                }
                e.setMessage(format);
            }

            public String getMessage(AsyncPlayerChatEvent e) {
                return map.get(e);
            }
        });
    }

    public void onLoad() {
        for (BubblePlugin plugin : pluginList) {
            plugin.onLoad();
        }
    }

    public void onDisable() {
        try {
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (BubblePlugin plugin : pluginList) {
            plugin.onDisable();
        }
        EnchantGlow.kill();
    }

    public void registerListener(Listener l) {
        getServer().getPluginManager().registerEvents(l, this);
    }

    public BubbleBarAPI getApi() {
        return api;
    }

    public VersionUTIL getUtil() {
        return util;
    }

    public MenuManager getManager() {
        return manager;
    }

    private class SimpleEntry<K, V> implements Map.Entry {
        private K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(Object value) {
            this.value = (V) value;
            return (V) value;
        }
    }
}
