package com.thebubblenetwork.api.framework;

import com.google.common.collect.ImmutableMap;
import com.thebubblenetwork.api.framework.commands.CommandPlugin;
import com.thebubblenetwork.api.framework.interaction.BubbleListener;
import com.thebubblenetwork.api.framework.messages.bossbar.BubbleBarAPI;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import com.thebubblenetwork.api.framework.plugin.BubblePluginLoader;
import com.thebubblenetwork.api.framework.plugin.PluginDescriptionFile;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.framework.util.version.VersionUTIL;
import com.thebubblenetwork.api.global.bubblepackets.PacketInfo;
import com.thebubblenetwork.api.global.bubblepackets.PacketListener;
import com.thebubblenetwork.api.global.bubblepackets.messaging.IPluginMessage;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.AssignMessage;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.RankDataUpdate;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import com.thebubblenetwork.api.global.file.PropertiesFile;
import com.thebubblenetwork.api.global.plugin.BubbleHub;
import com.thebubblenetwork.api.global.plugin.BubbleHubObject;
import com.thebubblenetwork.api.global.ranks.Rank;
import com.thebubblenetwork.api.global.sql.SQLConnection;
import com.thebubblenetwork.api.global.sql.SQLUtil;
import com.thebubblenetwork.api.global.type.ServerType;
import de.mickare.xserver.AbstractXServerManager;
import de.mickare.xserver.BukkitXServerManager;
import de.mickare.xserver.BukkitXServerPlugin;
import de.mickare.xserver.XServerManager;
import de.mickare.xserver.exceptions.NotInitializedException;
import de.mickare.xserver.net.XServer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Jacob on 09/12/2015.
 */

public class BubbleNetwork extends BubbleHubObject<JavaPlugin, Player> implements BubbleHub<JavaPlugin, Player> ,PacketListener{
    private static final Random random = new Random();
    protected static List<BubblePlugin> pluginList = new ArrayList<>();
    private static BubbleNetwork instance;
    private static String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "BubbleNetwork" +
            ChatColor.BLUE + "] " + ChatColor.GRAY;
    private static String chatFormat = "{prefix}{name}{suffix}{message}";
    public int FINALID;
    private BubbleBarAPI api;
    private VersionUTIL util;
    private MenuManager manager = new MenuManager();
    private ServerType type;
    private int id;
    private P plugin;
    private CommandPlugin commandPlugin;
    private XServer proxy;
    private BubblePlugin assigned;
    private BubbleListener listener = new BubbleListener(this);

    public BubbleNetwork(P plugin) {
        super();
        instance = this;
        this.plugin = plugin;
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

    public void onBubbleEnable() {
        api = new BubbleBarAPI();
        registerListener(getListener());
        commandPlugin.register(getPlugin());
        util = new VersionUTIL();
        EnchantGlow.getGlow();
        registerListener(api);
        api.DragonBarTask();
        registerListener(util);
        manager.register(getPlugin());
        for (BubblePlugin plugin : pluginList) {
            plugin.onEnable();
        }
    }

    public void onBubbleLoad() {
        commandPlugin = new CommandPlugin();
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        Map<PluginDescriptionFile, File> loaderList = new HashMap<>();
        List<PluginDescriptionFile> files = new ArrayList<>();
        for (File f : plugin.getDataFolder().listFiles()) {
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
            public int compare(PluginDescriptionFile o1, PluginDescriptionFile o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        for (PluginDescriptionFile file : files) {
            try {
                BubblePluginLoader loader = new BubblePluginLoader(loaderList.get(file), file);
                BubblePlugin bubblePlugin = loader.getPlugin();
                bubblePlugin.__init__(loader);
                register(bubblePlugin);
                logInfo("Loaded: " + plugin.getName());
            } catch (Exception e) {
                logSevere(e.getMessage());
                logSevere("Error loading " + file.getName() + "\n");
            }
        }
    }

    public void onBubbleDisable() {
        if(getAssigned() != null)getAssigned().onDisable();
        EnchantGlow.kill();
    }


    public BubbleBarAPI getBossApi() {
        return api;
    }

    public VersionUTIL getVersionUtil() {
        return util;
    }

    public void registerListener(Listener l) {
        getPlugin().getServer().getPluginManager().registerEvents(l, getPlugin());
    }

    public P getPlugin() {
        return plugin;
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

    public BubbleListener getListener(){
        return listener;
    }

    public void saveXServerDefaults() {
        int port;
        String ip;
        try {
            PropertiesFile thisserver = new PropertiesFile(new File("server.properties"));
            port = thisserver.getNumber("server-port").intValue();
            ip = (ip = thisserver.getString("server-ip")).equals("") ? "localhost" : ip;
        } catch (Exception e) {
            //Automatic Catch Statement
            logSevere(e.getMessage());
            endSetup("Could not load this servers properties");
            return;
        }
        this.FINALID = port-2000;
        File xserverfolder = new File("plugins" + File.separator + "Xserver");
        if(!xserverfolder.exists())xserverfolder.mkdir();
        File config = new File(xserverfolder + File.separator + "config.yml");
        if(!config.exists()) try {
            config.createNewFile();
        } catch (IOException e) {
            logSevere(e.getMessage());
            endSetup("Could not create xserver configuration");
        }
        YamlConfiguration xserverconfig = YamlConfiguration.loadConfiguration(config);
        xserverconfig.set("useMotdForServername", false);
        xserverconfig.set("servername", String.valueOf(FINALID));
        xserverconfig.set("mysql.User", getConnection().getUser());
        xserverconfig.set("mysql.Pass", getConnection().getPassword());
        xserverconfig.set("mysql.Data", getConnection().getDatabase());
        xserverconfig.set("mysql.Host", getConnection().getHostname());
        xserverconfig.set("mysql.Port", getConnection().getPort());
        xserverconfig.set("mysql.TableXServers", "xserver_servers");
        xserverconfig.set("mysql.TableXGroups", "xserver_groups");
        xserverconfig.set("mysql.TableXServersGroups", "xserver_servergroups");
        try {
            xserverconfig.save(config);
        } catch (IOException e) {
            //Automatic Catch Statement
            logSevere(e.getMessage());
            endSetup("Could not save xserver files, plugin is disabling");
        }
        logInfo("The plugin has loaded successfully, starting...");
    }

    public Player getPlayer(UUID uuid) {
        return null;
    }

    public void endSetup(String s) throws RuntimeException {
        getPlugin().getServer().shutdown();
        throw new RuntimeException(s);
    }

    public void logInfo(String s) {
        Logger l = getPlugin().getLogger();
        if(l != null)l.info(s);
        else System.out.println(s);
    }

    public void logSevere(String s) {
        Logger l = getPlugin().getLogger();
        if(l != null)l.severe(s);
        else System.err.println(s);
    }

    public BubblePlugin getPlugin(String s){
        for(BubblePlugin bubblePlugin:getPluginList()){
            if(bubblePlugin.getName().equalsIgnoreCase(s))return bubblePlugin;
        }
        return null;
    }

    public void onMessage(PacketInfo info, IPluginMessage message) {
        if(message instanceof AssignMessage){
            AssignMessage assignMessage = (AssignMessage)message;
            this.type = assignMessage.getWrapperType();
            this.id = assignMessage.getId();
            this.proxy = info.getServer();
            try {
                getPacketHub().sendMessage(info.getServer(),new AssignMessage(id,type));
            } catch (IOException e) {
                logSevere(e.getMessage());
            }
            if(getAssigned() != null){
                getAssigned().onDisable();
            }
            assigned = getPlugin(type.getName());
            getAssigned().onLoad();
            new BukkitRunnable(){
                public void run() {
                    getAssigned().onEnable();
                }
            }.runTask(getPlugin());
        }
        else if(message instanceof RankDataUpdate){
            RankDataUpdate rankDataUpdate = (RankDataUpdate)message;
            Rank.loadRank(rankDataUpdate.getName(),rankDataUpdate.getData());
        }
        else if(message instanceof PlayerDataResponse){
            PlayerDataResponse dataResponse = (PlayerDataResponse)message;
            getListener().getData().put(dataResponse.getUUID(),dataResponse.getData());
        }
    }

    public XServer getProxy() {
        return proxy;
    }

    public BubblePlugin getAssigned(){
        return assigned;
    }

    public ServerType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public AbstractXServerManager getXManager() {
        try {
            return BukkitXServerManager.getInstance();
        } catch (NotInitializedException e) {
            logSevere("Could not find XServer, looking for other ways");
        }
        Plugin p = getPlugin().getServer().getPluginManager().getPlugin("XServer");
        if(p == null){
            logSevere("Could not find XServer plugin, last try");
            p = BukkitXServerPlugin.getProvidingPlugin(BukkitXServerPlugin.class);
            if(p == null){
                endSetup("Could not find XServer");
            }
        }
        BukkitXServerPlugin xServerPlugin = (BukkitXServerPlugin)p;
        XServerManager manager = null;
        try {
            manager = xServerPlugin.getManager();
        } catch (Exception e) {
            logSevere(e.getMessage());
        }
        if(manager != null)return manager;
        logSevere("Could not find XServer, restarting");
        endSetup("Could not find XServer instance");
        throw new RuntimeException();
    }

    public void runTaskLater(Runnable runnable, long l, TimeUnit timeUnit) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(),runnable,timeUnit.toMillis(l)/50);
    }

    public void onConnect(PacketInfo info) {
    }

    public void onDisconnect(PacketInfo info) {
    }
}
