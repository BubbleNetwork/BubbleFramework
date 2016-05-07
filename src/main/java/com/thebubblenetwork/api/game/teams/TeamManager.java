package com.thebubblenetwork.api.game.teams;

import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamManager {

    private static List<UUID> redTeam = new ArrayList<>();
    private static List<UUID> blueTeam = new ArrayList<>();

    public TeamManager() {

    }

    public boolean isInTeam(Player player) {
        return redTeam.contains(player.getUniqueId()) || blueTeam.contains(player.getUniqueId());
    }

    public void addToTeam(TeamType type, Player player) {
        if (isInTeam(player)) {
            return;
        }
        switch (type) {
            case RED:
                redTeam.add(player.getUniqueId());
                break;
            case BLUE:
                blueTeam.add(player.getUniqueId());
                break;
        }
        player.sendMessage("You have joined the " + type.toString() + " team");

    }

    public void removeFromTeam(TeamType type, Player player) {
        if (!isInTeam(player)) {
            return;
        }
        switch (type) {
            case RED:
                if (redTeam.contains(player.getUniqueId())) {
                    redTeam.remove(player.getUniqueId());
                }
                break;
            case BLUE:
                if (blueTeam.contains(player.getUniqueId())) {
                    blueTeam.remove(player.getUniqueId());
                }
                break;
        }
    }

    public void assignTeams() {
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (i < Bukkit.getOnlinePlayers().size() / 2) {

                //add player to blue team
                addToTeam(TeamType.BLUE, player);

            } else {

                //add player to red team
                addToTeam(TeamType.RED, player);

            }
            i++;
        }
    }

    public void clearTeams() {
        redTeam.clear();
        blueTeam.clear();
    }

    public List<UUID> getRedTeam() {
        return redTeam;
    }

    public List<UUID> getBlueTeam() {
        return blueTeam;
    }

    public TeamType getTeamType(Player player) {
        if (!isInTeam(player)){
            return null;
        }

        return (redTeam.contains(player.getUniqueId()) ? TeamType.RED : TeamType.BLUE);

    }
}
