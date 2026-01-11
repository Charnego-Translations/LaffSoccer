package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PenaltyEvent extends GameEvent {
    public Match match;
}
