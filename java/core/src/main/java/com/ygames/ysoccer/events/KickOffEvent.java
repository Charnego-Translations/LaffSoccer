package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KickOffEvent extends GameEvent {
    public final Match.Period period;
}
