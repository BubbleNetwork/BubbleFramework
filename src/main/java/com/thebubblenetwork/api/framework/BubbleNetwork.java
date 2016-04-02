package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.event.PlayerDataReceivedEvent;
import com.thebubblenetwork.api.framework.interaction.DataRequestTask;
import com.thebubblenetwork.api.framework.listener.BubbleListener;
import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.plugin.loader.AddonDescriptionFile;
import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import com.thebubblenetwork.api.framework.plugin.loader.BubbleAddonLoader;
import com.thebubblenetwork.api.framework.plugin.manage.BukkitPlugman;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.global.bubblepackets.PacketInfo;
import com.thebubblenetwork.api.global.bubblepackets.PacketListener;
import com.thebubblenetwork.api.global.bubblepackets.messaging.IPluginMessage;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.AssignMessage;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.handshake.RankDataUpdate;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerDataRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerMoveRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerMoveTypeRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.ServerShutdownRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.PropertiesFile;
import com.thebubblenetwork.api.global.file.SSLUtil;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import com.thebubblenetwork.api.global.plugin.BubbleHub;
import com.thebubblenetwork.api.global.ranks.Rank;
import com.thebubblenetwork.api.global.type.ServerType;
import de.mickare.xserver.XServerPlugin;
import de.mickare.xserver.net.XServer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jacob on 09/12/2015.
 */

public class BubbleNetwork extends BubbleHub<JavaPlugin> implements PacketListener {
    private static final Random random = new Random();
    private static final int VERSION = 9;

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

    public static BubbleNetwork getInstance() {
        return instance;
    }

    private static BubbleNetwork instance;
    private static String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "BubbleNetwork" +
            ChatColor.BLUE + "] " + ChatColor.RESET;
    private static String chatFormat = "{prefix}{name}{suffix}{message}";
    public int FINALID;
    private ServerType type;
    private int id;
    private P plugin;
    private XServer proxy;
    private BubbleAddon assigned;
    private BubbleListener listener = new BubbleListener(this);
    private BukkitPlugman plugman;
    private Set<Listener> listeners = new HashSet<>();
    private Set<BukkitTask> executed = new HashSet<>();
    private Set<Menu> registeredMenus = new HashSet<>();
    private File file;

    public BubbleNetwork(P plugin) {
        super();
        instance = this;
        this.plugin = plugin;
    }

    public void registerListener(BubbleAddon plugin, Listener listener) {
        if (getAssigned() == plugin) {
            if (listeners.contains(listener)) {
                throw new IllegalArgumentException("Listener already registered");
            }
            listeners.add(listener);
            getPlugin().getServer().getPluginManager().registerEvents(listener, getPlugin());
        } else {
            throw new IllegalArgumentException("Plugin not registered");
        }
    }

    public void registerMenu(BubbleAddon addon, Menu menu) {
        if (getAssigned() == addon) {
            if (registeredMenus.contains(menu)) {
                throw new IllegalArgumentException("Menu already registered");
            }
            registeredMenus.add(menu);
        } else {
            throw new IllegalArgumentException("Plugin not registered");
        }
    }

    public void unregisterMenu(Menu menu) {
        if (registeredMenus.contains(menu)) {
            registeredMenus.remove(menu);
        } else {
            throw new IllegalArgumentException("Menu not registered");
        }
    }

    public void unregisterMenus() {
        registeredMenus.clear();
    }

    public BukkitTask registerRunnable(BubbleAddon plugin, Runnable r, TimeUnit unit, long time, boolean timer, boolean async) {
        if (getAssigned() == plugin) {
            long ticks = unit.toMillis(time) / 50;
            BukkitScheduler scheduler = getPlugin().getServer().getScheduler();
            BukkitTask task;
            if (async) {
                if (timer) {
                    task = scheduler.runTaskTimerAsynchronously(getPlugin(), r, ticks, ticks);
                } else {
                    task = scheduler.runTaskLater(getPlugin(), r, ticks);
                }
            } else if (timer) {
                task = scheduler.runTaskTimer(getPlugin(), r, ticks, ticks);
            } else {
                task = scheduler.runTaskLater(getPlugin(), r, ticks);
            }
            executed.add(task);
            return task;
        } else {
            throw new IllegalArgumentException("Plugin not registered");
        }
    }

    private void unregisterTasks() {
        for (BukkitTask task : executed) {
            try {
                task.cancel();
            } catch (Throwable throwable) {
            }
        }
        executed.clear();
    }

    private void unregisterListener() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
    }

    public Set<Menu> listMenu() {
        return new HashSet<>(registeredMenus);
    }

    public void onBubbleEnable() {
        getLogger().log(Level.INFO, "Loading components");

        file = getPlugin().getFile();
        registerListener(getListener());
        EnchantGlow.getGlow();
        plugman = new BukkitPlugman(getPlugin().getServer());
        getPacketHub().registerListener(this);
    }

    public void onBubbleLoad() {
        getLogger().log(Level.INFO, "Loading the plugin...");

        if (!getPlugin().getDataFolder().exists()) {
            getLogger().log(Level.INFO, "Creating addon folder");
            getPlugin().getDataFolder().mkdir();
        }

        getLogger().log(Level.INFO, "Finding addons...");
    }

    public void onBubbleDisable() {
        if (getAssigned() != null) {
            disableAddon();
        }
        EnchantGlow.kill();
        try {
            getPacketHub().sendMessage(getProxy(), new ServerShutdownRequest());
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not send shutdown request", e);
        }
        CitizensAPI.getNPCRegistry().deregisterAll();
    }

    private void registerListener(Listener l) {
        getPlugin().getServer().getPluginManager().registerEvents(l, getPlugin());
    }

    public P getPlugin() {
        return plugin;
    }

    public BubbleListener getListener() {
        return listener;
    }

    public void saveXServerDefaults() {
        int port;
        String ip;

        getLogger().log(Level.INFO, "Finding default server properties...");

        try {
            PropertiesFile thisserver = new PropertiesFile(new File("server.properties"));
            port = thisserver.getNumber("server-port").intValue();
            ip = (ip = thisserver.getString("server-ip")).equals("") ? "localhost" : ip;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load this servers properties", e);
            endSetup("Could not load this servers properties");
            return;
        }

        getLogger().log(Level.INFO, "Found default server properties");

        this.FINALID = port - 10000;

        getLogger().log(Level.INFO, "Creating xserver files");

        File xserverfolder = new File("plugins" + File.separator + "Xserver");

        if (!xserverfolder.exists()) {
            xserverfolder.mkdir();
        }

        File config = new File(xserverfolder + File.separator + "config.yml");

        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Could not create XServer configuration", e);
                endSetup("Could not create xserver configuration");
            }
        }

        getLogger().log(Level.INFO, "Loading XServer configuration...");

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

        getLogger().log(Level.INFO, "Saving XServer configuration...");

        try {
            xserverconfig.save(config);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not save xserver files", e);
            endSetup("Could not save xserver files");
        }

        getLogger().log(Level.INFO, "Saved XServer configuration");
    }

    public Player getPlayer(UUID uuid) {
        return getPlugin().getServer().getPlayer(uuid);
    }

    public void stop() {
        getPlugin().getServer().shutdown();
    }

    public Logger getLogger() {
        if (getPlugin() != null) {
            return getPlugin().getLogger();
        }
        return Logger.getLogger("Minecraft","BubbleNetwork");
    }

    public void disableAddon() {
        if (getAssigned() == null) {
            throw new IllegalArgumentException("No addon found");
        }
        getLogger().log(Level.INFO, "Disabling addon : " + getAssigned().getName());
        try {
            getAssigned().getLoader().close();
        } catch (Exception e) {
            getPlugin().getLogger().log(Level.WARNING, "Error while disabling plugin", e);
        }
        try{
            getAssigned().onDisable();
        } catch (Exception e) {
            getPlugin().getLogger().log(Level.WARNING, "Error while disabling plugin", e);
        }
        unregisterListener();
        unregisterTasks();
        unregisterMenus();
        File jar = assigned.getLoader().getJar();
        if(!jar.delete()){
            jar.deleteOnExit();
        }
        assigned = null;
    }

    public boolean enableAddon(ServerType type) {
        if (getAssigned() != null) {
            disableAddon();
        }
        try {
            SSLUtil.allowAnySSL();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error allowing all SSL connections", e);
        }
        if(!getPlugin().getDataFolder().exists()){
            getPlugin().getDataFolder().mkdir();
        }
        File jar = new File(getPlugin().getDataFolder(),type.getName() + ".jar");
        try{
            DownloadUtil.download(jar, type.getDownload(), getFileConnection());
        }
        catch (Exception ex){
            getLogger().log(Level.WARNING, "Error downloading addon", ex);
            return false;
        }
        AddonDescriptionFile file;
        try {
            file = BubbleAddonLoader.getPluginDescription(jar);
        } catch (InvalidDescriptionException e) {
            getLogger().log(Level.WARNING, "Error getting addon description file", e);
            return false;
        }
        BubbleAddonLoader loader;
        try{
            loader = new BubbleAddonLoader(jar, file);
        }
        catch (Exception ex){
            getLogger().log(Level.WARNING, "Failed to load addon", ex);
            return false;
        }
        BubbleAddon plugin = loader.getPlugin();
        plugin.__init__(loader);
        getLogger().log(Level.INFO, "{0} has been selected", file.getName());
        assigned = plugin;
        try {
            getAssigned().onLoad();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error whilst loading " + file.getName(), e);
            return false;
        }
        getLogger().log(Level.INFO, "{0} is loaded", file.getName());
        try {
            getAssigned().onEnable();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error whilst enabling " + file.getName(), e);
            return false;
        }
        getLogger().log(Level.INFO, "{0} is enabled", file.getName());
        return true;
    }

    public void onMessage(PacketInfo info, IPluginMessage message) {
        if (message instanceof AssignMessage) {
            getLogger().log(Level.INFO, "Received assign message");
            AssignMessage assignMessage = (AssignMessage) message;
            type = assignMessage.getWrapperType();
            id = assignMessage.getId();
            getLogger().log(Level.INFO, "ServerType: {0} ID: {1}", new Object[]{type.getName(), id});
            proxy = info.getServer();
            //Instant reply so Joinable update works
            try {
                getPacketHub().sendMessage(proxy, new AssignMessage(id, type));
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Could not send assign message", e);
                endSetup("Could not send assign message");
            }
            if(!enableAddon(type)){
                if(getAssigned() != null)disableAddon();
                endSetup("Could not setup addon");
            }
        } else if (message instanceof RankDataUpdate) {
            RankDataUpdate rankDataUpdate = (RankDataUpdate) message;
            Rank.loadRank(rankDataUpdate.getName(), rankDataUpdate.getData());
            getLogger().log(Level.INFO, "Loaded rank: " + rankDataUpdate.getName());
        } else if (message instanceof PlayerDataResponse) {
            final PlayerDataResponse dataResponse = (PlayerDataResponse) message;
            final Player player = Bukkit.getPlayer(dataResponse.getName());
            if (player != null) {
                final BukkitBubblePlayer bukkitBubblePlayer;
                if ((bukkitBubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId())) == null) {
                    getLogger().log(Level.WARNING, "Received data for a player which is not online " + dataResponse.getName());
                } else {
                    //Call event Async
                    new Thread(){
                        @Override
                        public void run() {
                            PlayerDataReceivedEvent event = new PlayerDataReceivedEvent(player, new PlayerData(dataResponse.getData()));
                            getPlugin().getServer().getPluginManager().callEvent(event);
                            bukkitBubblePlayer.setData(event.getData().getRaw());
                        }
                    }.start();
                }
            } else {
                DataRequestTask.setData(dataResponse.getName(), dataResponse.getData());
            }
        } else if (message instanceof PlayerDataRequest) {
            PlayerDataRequest request = (PlayerDataRequest) message;
            Player p = Bukkit.getPlayer(request.getName());
            if (p != null) {
                BubblePlayer<Player> player = BukkitBubblePlayer.getObject(p.getUniqueId());
                if (player != null) {
                    try {
                        getPacketHub().sendMessage(getProxy(), new PlayerDataResponse(request.getName(), player.getData().getRaw()));
                    } catch (IOException e1) {
                        getLogger().log(Level.WARNING, "Could not save playerdata for " + request.getName(), e1);
                    }
                } else {
                    getLogger().log(Level.WARNING, "BubblePlayer not found for data request " + request.getName());
                }
            } else {
                getLogger().log(Level.WARNING, "Player not found for data request " + request.getName());
            }
        } else {
            getLogger().log(Level.WARNING, "Unsupported message " + message.getType().getName());
        }
    }

    public XServer getProxy() {
        return proxy;
    }

    public BubbleAddon getAssigned() {
        return assigned;
    }

    public ServerType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public XServerPlugin getXPlugin() {
        if (!getPlugin().getServer().getPluginManager().isPluginEnabled("XServer")) {
            endSetup("Could not find XServer");
        }
        return (XServerPlugin) getPlugin().getServer().getPluginManager().getPlugin("XServer");
    }

    public void runTaskLater(Runnable runnable, long l, TimeUnit timeUnit) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), runnable, timeUnit.toMillis(l) / 50);
    }

    public File getReplace() {
        return file;
    }

    public String getArtifact() {
        return getPlugin().getName();
    }

    public int getVersion() {
        return VERSION;
    }

    public void update(Runnable update) {
        if (getAssigned() != null) {
            runTaskLater(update, getAssigned().finishUp(), TimeUnit.SECONDS);
        } else {
            runTaskLater(update, 0L, TimeUnit.SECONDS);
        }
    }

    public void sendPlayer(Player p, String server) {
        try {
            getPacketHub().sendMessage(getProxy(), new PlayerMoveRequest(p.getName(), server));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not move player ", e);
        }
    }

    public void sendPlayer(Player p, ServerType server) {
        try {
            getPacketHub().sendMessage(getProxy(), new PlayerMoveTypeRequest(p.getName(), server));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not move player ", e);
        }
    }

    public boolean bungee() {
        return false;
    }

    public void onConnect(PacketInfo info) {
    }

    public void onDisconnect(PacketInfo info) {
    }

    public void updateTaskBefore() {
        getPlugman().unload(getPlugin());
    }

    public void updateTaskAfter() {
        getPlugman().load(file);
    }

    public BukkitPlugman getPlugman() {
        return plugman;
    }
}
