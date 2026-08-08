package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;

public class YellowCardEvent extends GameEvent {

    public final Player player;

    public YellowCardEvent(Player player) {
        this.player = player;
    }
}
