package com.thebubblenetwork.api.framework.commands;

import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 11/12/2015.
 */
public class CommandPlugin implements Listener {
    public static List<BubbleCommand> getCmds() {
        return cmds;
    }

    public static boolean isFeatureEnabled() {
        return featureEnabled;
    }

    public static void setFeatureEnabled(boolean featureEnabled) {
        CommandPlugin.featureEnabled = featureEnabled;
    }

    private static boolean featureEnabled = true;
    private static List<BubbleCommand> cmds = new ArrayList<BubbleCommand>();
    private static String cmdNotFound = ChatColor.DARK_AQUA + "This command has not been found";
    private static String nopermission = ChatColor.DARK_PURPLE + "You do not have permission for this command";

    public void register(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!isFeatureEnabled()) {
            return;
        }
        BubblePlayer<Player> p = BukkitBubblePlayer.getObject(e.getPlayer().getUniqueId());
        String[] args = e.getMessage().replace("/", "").split(" ");
        BubbleCommand command = isCommmand(args[0]);
        if (command != null) {
            if (p.isAuthorized(command.getPermission())) {
                command.execute(p, args);
            } else {
                p.getPlayer().sendMessage(nopermission);
            }
        } else {
            p.getPlayer().sendMessage(cmdNotFound);
        }
        e.setCancelled(true);
    }

    protected BubbleCommand isCommmand(String used) {
        for (BubbleCommand bubbleCommand : cmds) {
            if (bubbleCommand.getName().equalsIgnoreCase(used)) {
                return bubbleCommand;
            } else {
                for (String s : bubbleCommand.getAliases()) {
                    if (s.equalsIgnoreCase(used)) {
                        return bubbleCommand;
                    }
                }
            }
        }
        return null;
    }
}
