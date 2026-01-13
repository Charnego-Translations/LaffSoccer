package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Match;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeeperSaveEvent extends GameEvent {
    public Match match;
}
