package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Goal;

public class HomeGoalEvent extends GameEvent {

    public Goal goal;

    public HomeGoalEvent(Goal goal) {
        this.goal = goal;
    }
}
