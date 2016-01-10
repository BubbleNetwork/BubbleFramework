package com.thebubblenetwork.api.game.maps;

/**
 * Created by Jacob on 13/12/2015.
 */
public class Vote {
    private String voted;

    public Vote(GameMap voted) {
        this.voted = voted.getName();
    }

    public void setVote(GameMap map) {
        this.voted = map.getName();
    }

    public GameMap getMap() {
        for (GameMap map : GameMap.getMaps())
            if (map.getName().equals(voted))
                return map;
        return null;
    }
}
