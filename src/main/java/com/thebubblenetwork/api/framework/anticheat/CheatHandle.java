package com.thebubblenetwork.api.framework.anticheat;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.event.PlayerViolationSendEvent;
import com.thebubblenetwork.api.global.anticheat.ViolationWrapper;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.AntiCheatViolationMessage;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.components.NoCheatPlusAPI;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class CheatHandle implements NCPHook{
    private static final double VERSION = 1.0;

    private NoCheatPlusAPI api;

    public CheatHandle(JavaPlugin uncast){
        this((NoCheatPlusAPI)uncast);
    }

    private CheatHandle(NoCheatPlusAPI noCheatPlus){
        this.api = noCheatPlus;
        NCPHookManager.addHook(CheckType.ALL, this);
    }

    public boolean onCheckFailure(final CheckType checkType, final Player player, final IViolationInfo iViolationInfo) {
        final PlayerViolationSendEvent event = new PlayerViolationSendEvent(player, checkType, iViolationInfo);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            new Thread(){
                public void run() {
                    try {
                        BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(), new AntiCheatViolationMessage(new ViolationWrapper(event.getPlayer().getName(), event.getType().getName(), event.getInfo().getTotalVl(), event.getInfo().getTotalVl())));
                    } catch (IOException e) {
                    }
                }
            }.start();
        }
        return false;
    }

    public String getHookName() {
        return "BubbleHook";
    }

    public String getHookVersion() {
        return String.valueOf(VERSION);
    }
}
