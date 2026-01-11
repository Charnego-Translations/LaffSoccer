package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CornerKickEvent extends GameEvent {
    public Match match;
}
