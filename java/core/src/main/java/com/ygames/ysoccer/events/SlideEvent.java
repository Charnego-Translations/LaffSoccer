package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SlideEvent extends GameEvent {
    public Player player;
}
