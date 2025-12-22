package com.ygames.ysoccer.match;

public class Goal {

    public enum Type {NORMAL, OWN_GOAL, PENALTY}

    public Player player;
    public int minute;
    public Type type;

    public Goal(Player player, int minute, Type type) {
        this.player = player;
        this.minute = minute;
        this.type = type;
    }
}
