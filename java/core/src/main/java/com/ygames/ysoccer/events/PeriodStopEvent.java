package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchFsm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PeriodStopEvent extends GameEvent {
    public Match match;
    public MatchFsm.StateId sceneState;
}
