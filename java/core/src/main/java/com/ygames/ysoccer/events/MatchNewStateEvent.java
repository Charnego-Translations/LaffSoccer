package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;

public class MatchNewStateEvent extends GameEvent {

    public Match match;

    public MatchNewStateEvent(Match match) {
        this.match = match;
    }
}
