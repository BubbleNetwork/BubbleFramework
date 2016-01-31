package com.thebubblenetwork.api.framework.messages.bossbar;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.messages.bossbar.bars.FDragon;
import com.thebubblenetwork.api.framework.messages.bossbar.bars.FWither;
import com.thebubblenetwork.api.framework.util.version.VersionUTIL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;


/**
 * Thanks for chasechocolate and BigTeddy98 for the tutorial
 * it is based on the code by them and some other tutorial
 * https://forums.bukkit.org/threads/util-set-a-players-boss-bar-nms.245073/
 * https://forums.bukkit.org/threads/tutorial-utilizing-the-boss-health-bar.158018/
 *
 * @author Marzouki Ghofrane , mgone CraftZone.fr
 */


public class BubbleBarAPI implements Listener {

    public static BubbleBarAPI plugin;
    public static Map<Player, String> playerdragonbartask = new HashMap<Player, String>();
    public static Map<Player, Float> healthdragonbartask = new HashMap<Player, Float>();
    public static Map<Player, Integer> cooldownsdragonbar = new HashMap<Player, Integer>();
    public static Map<Player, Integer> starttimerdragonbar = new HashMap<Player, Integer>();

    public static Map<Player, String> playerwitherbartask = new HashMap<Player, String>();
    public static Map<Player, Float> healthwitherbartask = new HashMap<Player, Float>();
    public static Map<Player, Integer> cooldownswitherbar = new HashMap<Player, Integer>();
    public static Map<Player, Integer> starttimerwitherbar = new HashMap<Player, Integer>();

    public BubbleBarAPI() {
        plugin = this;
    }

    public static BubbleBarAPI getInstance() {
        return plugin;
    }

    //dragon
    public static void setBarDragon(Player p, String text) {
        playerdragonbartask.put(p, text);
        FDragon.setBossBartext(p, text);
    }

    public static void setBarDragonHealth(Player p, String text, float health) {
        if (health <= 0) {
            health = 1F;
        } else if (health > 100) {
            health = 100F;
        }
        playerdragonbartask.put(p, text);
        healthdragonbartask.put(p, (health / 100) * 200);
        FDragon.setBossBar(p, text, health);
    }

    public static void setBarDragonTimer(Player p, String text, int timer) {
        playerdragonbartask.put(p, text);
        cooldownsdragonbar.put(p, timer);
        if (!starttimerdragonbar.containsKey(p))
            starttimerdragonbar.put(p, timer);
        int unite = Math.round(200 / starttimerdragonbar.get(p));
        FDragon.setBossBar(p, text, unite * timer);
    }

    public static void removeBarDragon(Player p) {
        playerdragonbartask.remove(p);
        healthdragonbartask.remove(p);
        cooldownsdragonbar.remove(p);
        starttimerdragonbar.remove(p);
        FDragon.removeBossBar(p);
    }

    public static boolean hasBarDragon(Player p) {
        return playerdragonbartask.get(p) != null;
    }

    public static String getMessageDragon(Player p) {
        if (playerdragonbartask.containsKey(p))
            return playerdragonbartask.get(p);
        else
            return " ";
    }

    //wither
    public static void setBarWither(Player p, String text) {
        playerwitherbartask.put(p, text);
        FWither.setBossBartext(p, text);
    }

    public static void setBarWitherHealth(Player p, String text, float health) {
        if (health <= 0) {
            health = 1;
        } else if (health > 100) {
            health = 100;
        }
        playerwitherbartask.put(p, text);
        healthwitherbartask.put(p, (health / 100) * 300);
        FWither.setBossBar(p, text, health);
    }

    public static void setBarWitherTimer(Player p, String text, int timer) {
        playerwitherbartask.put(p, text);
        cooldownswitherbar.put(p, timer);
        if (!starttimerwitherbar.containsKey(p))
            starttimerwitherbar.put(p, timer);
        int unite = Math.round(300 / starttimerwitherbar.get(p));
        FWither.setBossBar(p, text, unite * timer);
    }

    public static void removeBarWither(Player p) {
        playerwitherbartask.remove(p);
        healthwitherbartask.remove(p);
        cooldownswitherbar.remove(p);
        starttimerwitherbar.remove(p);
        FWither.removeBossBar(p);
    }

    public static boolean hasBarWither(Player p) {
        return playerwitherbartask.get(p) != null;
    }

    public static String getMessageWither(Player p) {
        if (playerwitherbartask.containsKey(p))
            return playerwitherbartask.get(p);
        else
            return " ";
    }

    //both
    public static void setBar(Player p, String text) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V18) {
            playerwitherbartask.put(p, text);
            FWither.setBossBartext(p, text);
        }

        playerdragonbartask.put(p, text);
        FDragon.setBossBartext(p, text);
    }

    public static void setBarHealth(Player p, String text, float health) {
        if (health <= 0) {
            health = 1;
        } else if (health > 100) {
            health = 100;
        }
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V18) {
            playerwitherbartask.put(p, text);
            healthwitherbartask.put(p, (health / 100) * 300);
            FWither.setBossBar(p, text, health);
        }

        playerdragonbartask.put(p, text);
        healthdragonbartask.put(p, (health / 100) * 200);
        FDragon.setBossBar(p, text, health);
    }

    public static void setBarTimer(Player p, String text, int timer) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V18) {
            playerwitherbartask.put(p, text);
            cooldownswitherbar.put(p, timer);
            if (!starttimerwitherbar.containsKey(p))
                starttimerwitherbar.put(p, timer);
            int unite = Math.round(300 / starttimerwitherbar.get(p));
            FWither.setBossBar(p, text, unite * timer);
        }
        playerdragonbartask.put(p, text);
        cooldownsdragonbar.put(p, timer);
        if (!starttimerdragonbar.containsKey(p))
            starttimerdragonbar.put(p, timer);
        int unite1 = Math.round(200 / starttimerdragonbar.get(p));
        FDragon.setBossBar(p, text, unite1 * timer);

    }

    public static void removeBar(Player p) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V18) {
            playerwitherbartask.remove(p);
            healthwitherbartask.remove(p);
            cooldownswitherbar.remove(p);
            starttimerwitherbar.remove(p);
            FWither.removeBossBar(p);
        }

        playerdragonbartask.remove(p);
        healthdragonbartask.remove(p);
        cooldownsdragonbar.remove(p);
        starttimerdragonbar.remove(p);
        FDragon.removeBossBar(p);
    }

    public static boolean hasBar(Player p) {
        if (VersionUTIL.getVersion(p) == VersionUTIL.Version.V18)
            return (playerwitherbartask.containsKey(p) && playerdragonbartask.containsKey(p));
        else
            return playerdragonbartask.get(p) != null;
    }

    public static String getMessage(Player p) {
        if (playerdragonbartask.containsKey(p))
            return playerdragonbartask.get(p);
        else
            return " ";
    }


    public void DragonBarTask() {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!cooldownsdragonbar.containsKey(p)) {
                        if (playerdragonbartask.containsKey(p) && !healthdragonbartask.containsKey(p)) {
                            setBarDragon(p, playerdragonbartask.get(p));
                        } else if (playerdragonbartask.containsKey(p) && healthdragonbartask.containsKey(p)) {
                            setBarDragonHealth(p, playerdragonbartask.get(p), healthdragonbartask.get(p));
                        }
                    }
                    if (!cooldownswitherbar.containsKey(p)) {
                        if (playerwitherbartask.containsKey(p) && !healthwitherbartask.containsKey(p)) {
                            setBarWither(p, playerwitherbartask.get(p));
                        } else if (playerwitherbartask.containsKey(p) && healthwitherbartask.containsKey(p)) {
                            setBarWitherHealth(p, playerwitherbartask.get(p), healthwitherbartask.get(p));
                        }
                    }
                }
            }
        }.runTaskTimer(BubbleNetwork.getInstance().getPlugin(), 0, 40);
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (cooldownsdragonbar.containsKey(p)) {
                        if (cooldownsdragonbar.get(p) > 0) {
                            cooldownsdragonbar.put(p, cooldownsdragonbar.get(p) - 1);
                            setBarDragonTimer(p, playerdragonbartask.get(p), cooldownsdragonbar.get(p));
                        } else
                            removeBarDragon(p);
                    }

                    if (cooldownswitherbar.containsKey(p)) {
                        if (cooldownswitherbar.get(p) > 0) {
                            cooldownswitherbar.put(p, cooldownswitherbar.get(p) - 1);
                            setBarWitherTimer(p, playerwitherbartask.get(p), cooldownswitherbar.get(p));
                        } else
                            removeBarWither(p);
                    }
                }
            }
        }.runTaskTimer(BubbleNetwork.getInstance().getPlugin(), 0, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        removeBar(p);
        FDragon.removehorligneD(p);
        FWither.removehorligneW(p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerKick(PlayerKickEvent event) {
        Player p = event.getPlayer();
        removeBar(p);
        FDragon.removehorligneD(p);
        FWither.removehorligneW(p);
    }
}
