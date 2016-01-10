package com.thebubblenetwork.api.framework.commands;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BubblePlayer;
import com.thebubblenetwork.api.framework.plugin.BubblePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 11/12/2015.
 */
public class CommandPlugin extends BubblePlugin implements Listener {
    private static boolean featureEnabled = true;
    private static List<BubbleCommand> cmds = new ArrayList<BubbleCommand>();
    private static String cmdNotFound = ChatColor.RED + "" + ChatColor.BOLD + "Command not found";
    private static String nopermission = ChatColor.RED + "" + ChatColor.BOLD + "You do not have permission for this " +
            "command";

    static {
        CommandPlugin cmd = new CommandPlugin();
        BubbleNetwork.register(cmd);
    }

    private File f = new File(getDataFolder() + File.separator + "Plugins");

    public static List<BubbleCommand> getCmds() {
        return cmds;
    }

    public static boolean isFeatureEnabled() {
        return featureEnabled;
    }

    public static void setFeatureEnabled(boolean featureEnabled) {
        CommandPlugin.featureEnabled = featureEnabled;
    }

    public void onLoad() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        if (!f.exists())
            f.mkdir();
    }

    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!isFeatureEnabled())
            return;
        BubblePlayer p = BubblePlayer.get(e.getPlayer());
        String[] args = e.getMessage().replace("/", "").split(" ");
        BubbleCommand command = isCommmand(args[0]);
        if (command != null) {
            if (p.isAuthorized(command)) {
                command.execute(p, args);
            }
            else {
                p.getPlayer().sendMessage(nopermission);
            }
        }
        else {
            p.getPlayer().sendMessage(cmdNotFound);
        }
        e.setCancelled(true);
    }

    protected BubbleCommand isCommmand(String used) {
        for (BubbleCommand bubbleCommand : cmds) {
            if (bubbleCommand.getName().equalsIgnoreCase(used))
                return bubbleCommand;
            else {
                for (String s : bubbleCommand.getAliases()) {
                    if (s.equalsIgnoreCase(used))
                        return bubbleCommand;
                }
            }
        }
        return null;
    }
}
