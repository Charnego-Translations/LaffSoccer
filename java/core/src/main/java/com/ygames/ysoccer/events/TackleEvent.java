package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TackleEvent extends GameEvent {
    public boolean isFault;
    public Player player;
    public Player opponent;
}
