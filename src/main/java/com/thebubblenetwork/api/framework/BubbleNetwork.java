package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.commands.CommandPlugin;
import com.thebubblenetwork.api.framework.interaction.BubbleListener;
import com.thebubblenetwork.api.framework.interaction.DataRequestTask;
import com.thebubblenetwork.api.framework.messages.bossbar.BubbleBarAPI;
import com.thebubblenetwork.api.framework.plugin.AddonDescriptionFile;
import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import com.thebubblenetwork.api.framework.plugin.BubbleAddonLoader;
import com.thebubblenetwork.api.framework.plugin.BukkitPlugman;
import com.thebubblenetwork.api.framework.util.mc.items.EnchantGlow;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.api.framework.util.mc.menu.MenuManager;
import com.thebubblenetwork.api.framework.util.version.VersionUTIL;
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
import com.thebubblenetwork.api.global.file.PropertiesFile;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import com.thebubblenetwork.api.global.plugin.BubbleHub;
import com.thebubblenetwork.api.global.plugin.BubbleHubObject;
import com.thebubblenetwork.api.global.ranks.Rank;
import com.thebubblenetwork.api.global.type.ServerType;
import de.mickare.xserver.XServerPlugin;
import de.mickare.xserver.net.XServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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

public class BubbleNetwork extends BubbleHubObject<JavaPlugin> implements BubbleHub<JavaPlugin> ,PacketListener{
    private static final Random random = new Random();
    private static final int VERSION = 9;

    protected static Map<AddonDescriptionFile, File> loaderList = new HashMap<>();
    private static BubbleNetwork instance;
    private static String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "BubbleNetwork" +
            ChatColor.BLUE + "] " + ChatColor.GRAY;
    private static String chatFormat = "{prefix}{name}{suffix}{message}";
    public int FINALID;
    private BubbleBarAPI api;
    private VersionUTIL util;
    private ServerType type;
    private int id;
    private P plugin;
    private CommandPlugin commandPlugin;
    private XServer proxy;
    private BubbleAddon assigned;
    private BubbleListener listener = new BubbleListener(this);
    private BukkitPlugman plugman;
    private Set<Listener> listeners = new HashSet<>();
    private Set<BukkitTask> executed = new HashSet<>();
    private Set<Menu> registeredMenus = new HashSet<>();
    private MenuManager manager = new MenuManager();

    public BubbleNetwork(P plugin) {
        super();
        instance = this;
        this.plugin = plugin;
    }

    public void registerListener(BubbleAddon plugin, Listener listener){
        if(getAssigned() == plugin){
            if(listeners.contains(listener))throw new IllegalArgumentException("Listener already registered");
            listeners.add(listener);
            getPlugin().getServer().getPluginManager().registerEvents(listener,getPlugin());
        }
        else throw new IllegalArgumentException("Plugin not registered");
    }

    public void registerMenu(BubbleAddon addon, Menu menu){
        if(getAssigned() == addon){
            if(registeredMenus.contains(menu))throw new IllegalArgumentException("Menu already registered");
            registeredMenus.add(menu);
        }
        else throw new IllegalArgumentException("Plugin not registered");
    }

    public void unregisterMenu(Menu menu){
        if(registeredMenus.contains(menu)){
            registeredMenus.remove(menu);
        }
        else throw new IllegalArgumentException("Menu not registered");
    }

    public void unregisterMenus(){
        registeredMenus.clear();
    }

    public BukkitTask registerRunnable(BubbleAddon plugin, Runnable r, TimeUnit unit, long time, boolean timer, boolean async){
        if(getAssigned() == plugin){
            long ticks = unit.toMillis(time)/50;
            BukkitScheduler scheduler = getPlugin().getServer().getScheduler();
            BukkitTask task;
            if(async) {
                if (timer) task = scheduler.runTaskTimerAsynchronously(getPlugin(), r, ticks, ticks);
                else task = scheduler.runTaskLater(getPlugin(), r, ticks);
            }
            else if(timer) task = scheduler.runTaskTimer(getPlugin(),r,ticks,ticks);
            else task = scheduler.runTaskLater(getPlugin(),r,ticks);
            executed.add(task);
            return task;
        }
        else throw new IllegalArgumentException("Plugin not registered");
    }

    private void unregisterTasks(){
        for(BukkitTask task:executed){
            try {
                task.cancel();
            }
            catch (Throwable throwable){
            }
        }
        executed.clear();
    }

    private void unregisterListener(){
        for(Listener listener: listeners){
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
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

    public static Map<AddonDescriptionFile, File> getLoaderList() {
        return loaderList;
    }

    public static BubbleNetwork getInstance() {
        return instance;
    }

    @Deprecated
    public Set<Menu> listMenu(){
        return registeredMenus;
    }

    public static BubbleAddonLoader register(AddonDescriptionFile plugin) {
        if(plugin == null)throw new IllegalArgumentException("Plugin cannot be null");
        File file = getLoaderList().get(plugin);
        if(file == null)throw new IllegalArgumentException("Could not find file");
        try {
            return new BubbleAddonLoader(file,plugin);
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }

    public void onBubbleEnable() {
        logInfo("Loading components");

        api = new BubbleBarAPI();
        registerListener(getListener());
        commandPlugin.register(getPlugin());
        util = new VersionUTIL();
        EnchantGlow.getGlow();
        registerListener(api);
        api.DragonBarTask();
        registerListener(util);
        manager.register(getPlugin());
        getPacketHub().registerListener(this);
        plugman = new BukkitPlugman(getPlugin().getServer());

        logInfo("Components have been loaded");
    }

    public void onBubbleLoad() {
        commandPlugin = new CommandPlugin();

        logInfo("Finding addon folder");

        if (!getPlugin().getDataFolder().exists()){
            logInfo("Creating addon folder");
            getPlugin().getDataFolder().mkdir();
        }

        logInfo("Finding addons");

        if(!getPlugin().getDataFolder().isDirectory())endSetup("Addon folder is not a directory");

        for (File f : getPlugin().getDataFolder().listFiles()) {
            if (f.getName().endsWith(".jar")) {
                try {
                    AddonDescriptionFile file = BubbleAddonLoader.getPluginDescription(f);
                    getLoaderList().put(file,f);
                } catch (Throwable ex) {
                    getLogger().log(Level.WARNING,"Error whilst finding addon " + f.getName(),ex);
                }
            }
        }
        logInfo("Finished finding addons");
    }

    public void onBubbleDisable() {
        if(getAssigned() != null)getAssigned().onDisable();
        EnchantGlow.kill();
        try {
            getPacketHub().sendMessage(getProxy(),new ServerShutdownRequest());
        } catch (IOException e) {
            logSevere(e.getMessage());
            logSevere("Could not send shutdown request");
        }
    }


    public BubbleBarAPI getBossApi() {
        return api;
    }

    public VersionUTIL getVersionUtil() {
        return util;
    }

    private void registerListener(Listener l) {
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

        logInfo("Finding default server properties...");

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

        logInfo("Found default server properties");

        this.FINALID = port-10000;

        logInfo("Creating xserver files");

        File xserverfolder = new File("plugins" + File.separator + "Xserver");

        if(!xserverfolder.exists())xserverfolder.mkdir();

        File config = new File(xserverfolder + File.separator + "config.yml");

        if(!config.exists()) try {
            config.createNewFile();
        } catch (IOException e) {
            logSevere(e.getMessage());
            endSetup("Could not create xserver configuration");
        }

        logInfo("Loading XServer configuration...");

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

        logInfo("Loaded XServer configuration");

        logInfo("Saving XServer configuration...");

        try {
            xserverconfig.save(config);
        } catch (IOException e) {
            //Automatic Catch Statement
            logSevere(e.getMessage());
            endSetup("Could not save xserver files, plugin is disabling");
        }

        logSevere("Saved XServer configuration");
    }

    public Player getPlayer(UUID uuid) {
        return getPlugin().getServer().getPlayer(uuid);
    }

    public void endSetup(String s) throws RuntimeException {
        getPlugin().getServer().shutdown();
        throw new RuntimeException(s);
    }

    public Logger getLogger(){
        if(getPlugin() != null && getPlugin().isEnabled())return getPlugin().getLogger();
        return null;
    }

    public void logInfo(String s) {
        Logger l = getLogger();
        if(l != null)l.info(s);
        else System.out.println("[BubbleFramework] " + s);
    }

    public void logSevere(String s) {
        Logger l = getLogger();
        if(l != null)l.severe(s);
        else System.err.println("[BubbleFramework] " + s);
    }

    public AddonDescriptionFile getPlugin(String s){
        for(AddonDescriptionFile bubblePlugin:getLoaderList().keySet()){
            if(bubblePlugin.getName().equalsIgnoreCase(s))return bubblePlugin;
        }
        return null;
    }

    public void disableAddon(){
        if(getAssigned() == null)throw new IllegalArgumentException("No addon found");
        logInfo("Disabling addon : " + getAssigned().getName());
        try {
            getAssigned().getLoader().close();
        } catch (IOException e) {
            getPlugin().getLogger().log(Level.WARNING,"Error while disabling plugin");
        }
        getAssigned().onDisable();
        unregisterListener();
        unregisterTasks();
        unregisterMenus();
        assigned = null;
    }

    public void enableAddon(AddonDescriptionFile addonfile){
        if(getAssigned() != null)disableAddon();
        if(addonfile == null){
            endSetup("Could not find assigned addon");
        }
        BubbleAddonLoader loader = register(addonfile);
        if(loader == null){
            endSetup("Could not find assigned addon");
            return;
        }
        BubbleAddon plugin = loader.getPlugin();
        plugin.__init__(loader);
        logInfo("Enabling addon: " + plugin.getName());
        assigned = plugin;
        getAssigned().onLoad();
        logInfo("Addon is loaded");
        getAssigned().onEnable();
        logInfo("Addon is enabled");
    }

    public void onMessage(PacketInfo info, IPluginMessage message) {
        if(message instanceof AssignMessage){
            logInfo("Received assign message");
            AssignMessage assignMessage = (AssignMessage)message;
            this.type = assignMessage.getWrapperType();
            this.id = assignMessage.getId();
            logInfo("ServerType: " + getType().getName() + " ID: " + String.valueOf(id));
            this.proxy = info.getServer();
            AddonDescriptionFile addonfile = getPlugin(getType().getName());
            enableAddon(addonfile);
            try {
                getPacketHub().sendMessage(info.getServer(),new AssignMessage(id,type));
            } catch (IOException e) {
                logSevere(e.getMessage());
                endSetup("Could not send assign message");
            }
            logInfo("Sent assign message!");
        }
        else if(message instanceof RankDataUpdate){
            RankDataUpdate rankDataUpdate = (RankDataUpdate)message;
            Rank.loadRank(rankDataUpdate.getName(),rankDataUpdate.getData());
            logInfo("Loaded rank: " + rankDataUpdate.getName());
        }
        else if(message instanceof PlayerDataResponse){
            PlayerDataResponse dataResponse = (PlayerDataResponse)message;
            Player player = Bukkit.getPlayer(dataResponse.getName());
            if(player != null) {
                BubblePlayer<Player> bukkitBubblePlayer;
                if((bukkitBubblePlayer = BukkitBubblePlayer.getObject(player.getUniqueId())) == null) {
                    logSevere("Received data for a player which is not online " + dataResponse.getName());
                }
                else bukkitBubblePlayer.setData(dataResponse.getData());
            }
            else DataRequestTask.setData(dataResponse.getName(),dataResponse.getData());
        }
        else if(message instanceof PlayerDataRequest){
            PlayerDataRequest request = (PlayerDataRequest)message;
            Player p = Bukkit.getPlayer(request.getName());
            if(p != null) {
                BubblePlayer<Player> player = BukkitBubblePlayer.getObject(p.getUniqueId());
                if(player != null) {
                    try {
                        getPacketHub().sendMessage(getProxy(), new PlayerDataResponse(request.getName(), player.getData().getRaw()));
                    } catch (IOException e1) {
                        logSevere(e1.getMessage());
                        logSevere("Could not save playerdata for " + request.getName());
                    }
                }
                else logSevere("BubblePlayer not found for data request " + request.getName());
            }
            else logSevere("Player not found for data request " + request.getName());
        }
        else logSevere("Unsupported message " + message.getType().getName());
    }

    public XServer getProxy() {
        return proxy;
    }

    public BubbleAddon getAssigned(){
        return assigned;
    }

    public ServerType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public XServerPlugin getXPlugin() {
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("XServer"))endSetup("Could not find XServer");
        return (XServerPlugin) getPlugin().getServer().getPluginManager().getPlugin("XServer");
    }

    public void runTaskLater(Runnable runnable, long l, TimeUnit timeUnit) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(),runnable,timeUnit.toMillis(l)/50);
    }

    public File getReplace() {
        return getPlugin().getFile();
    }

    public String getArtifact() {
        return getPlugin().getName();
    }

    public int getVersion() {
        return VERSION;
    }

    public void update(Runnable update) {
        if(getAssigned() != null){
            runTaskLater(update,getAssigned().finishUp(),TimeUnit.SECONDS);
        }
        else runTaskLater(update,0L,TimeUnit.SECONDS);
    }

    public void sendPlayer(Player p,String server){
        try {
            getPacketHub().sendMessage(getProxy(),new PlayerMoveRequest(p.getName(),server));
        } catch (IOException e) {
            logSevere("Could not move player " + e.getMessage());
        }
    }

    public void sendPlayer(Player p,ServerType server){
        try {
            getPacketHub().sendMessage(getProxy(),new PlayerMoveTypeRequest(p.getName(),server));
        } catch (IOException e) {
            logSevere("Could not move player " + e.getMessage());
        }
    }

    public boolean bungee(){
        return false;
    }

    public void onConnect(PacketInfo info) {
    }

    public void onDisconnect(PacketInfo info) {
    }

    public BukkitPlugman getPlugman() {
        return plugman;
    }
}
