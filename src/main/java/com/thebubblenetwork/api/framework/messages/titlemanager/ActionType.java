package com.thebubblenetwork.api.framework.messages.titlemanager;

public enum ActionType {
    CHAT(1),
    ACTION(2);

    private int data;

    ActionType(int data){
        this.data = data;
    }

    public int getData(){
        return data;
    }
}
