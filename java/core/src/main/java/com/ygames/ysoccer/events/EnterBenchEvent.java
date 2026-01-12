package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnterBenchEvent extends GameEvent {
    public Match match;
    public Team team;
}
