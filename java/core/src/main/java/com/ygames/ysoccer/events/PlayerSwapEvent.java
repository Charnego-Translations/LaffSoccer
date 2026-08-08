package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Team;

public class PlayerSwapEvent extends GameEvent {

    public final Team team;
    public final int lineupIndexA;
    public final int lineupIndexB;

    public PlayerSwapEvent(Team team, int lineupIndexA, int lineupIndexB) {
        this.team = team;
        this.lineupIndexA = lineupIndexA;
        this.lineupIndexB = lineupIndexB;
    }
}
