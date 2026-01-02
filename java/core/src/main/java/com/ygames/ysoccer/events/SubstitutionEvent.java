package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubstitutionEvent extends GameEvent {
    public Team team;
    public Player in;
    public Player out;
}
