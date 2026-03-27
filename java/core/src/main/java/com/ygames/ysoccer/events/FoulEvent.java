package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;

public class FoulEvent extends GameEvent {

    public Match.Foul foul;

    public FoulEvent(Match.Foul foul) {
        this.foul = foul;
    }
}
