package com.thebubblenetwork.api.framework.messages.titlemanager;

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public enum TitleType {
    TITLE(PacketPlayOutTitle.EnumTitleAction.TITLE),
    SUBTITLE(PacketPlayOutTitle.EnumTitleAction.SUBTITLE),
    TIMES(PacketPlayOutTitle.EnumTitleAction.TIMES),
    CLEAR(PacketPlayOutTitle.EnumTitleAction.CLEAR),
    RESET(PacketPlayOutTitle.EnumTitleAction.RESET);

    private PacketPlayOutTitle.EnumTitleAction action;

    TitleType(PacketPlayOutTitle.EnumTitleAction action){
        this.action = action;
    }

    public PacketPlayOutTitle.EnumTitleAction getAction(){
        return action;
    }
}
