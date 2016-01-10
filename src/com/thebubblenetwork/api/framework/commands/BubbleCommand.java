package com.thebubblenetwork.api.framework.commands;

import com.thebubblenetwork.api.framework.BubblePlayer;
import com.thebubblenetwork.api.framework.ranks.Rank;

/**
 * Created by Jacob on 11/12/2015.
 */
public abstract class BubbleCommand {
    private String name, description, permission;
    private CommandType type;
    private String[] aliases;

    public BubbleCommand(String name, String description, String permission, CommandType type, String... aliases) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.permission = permission;
        this.aliases = aliases;
    }

    public static boolean isAuthorized(Rank r, String permissionraw, CommandType type) {
        String typebase = type.toString().toLowerCase();
        return r.isAuthorized(typebase + ".*") || r.isAuthorized(typebase + "." + permissionraw);
    }

    public abstract void execute(BubblePlayer sender, String[] args);

    public String[] getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}