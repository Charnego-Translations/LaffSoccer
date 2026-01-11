package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Goal;
import com.ygames.ysoccer.match.Match;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HomeGoalEvent extends GameEvent {
    public Match match;
    public Goal goal;
}
