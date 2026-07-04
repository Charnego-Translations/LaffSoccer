package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerSwapEvent extends GameEvent {
    public Team team;
    public Player player1;
    public Player player2;
}
