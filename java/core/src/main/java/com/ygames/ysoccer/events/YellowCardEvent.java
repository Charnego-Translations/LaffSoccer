package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class YellowCardEvent extends GameEvent {
    public Match match;
    public Player player;
}
