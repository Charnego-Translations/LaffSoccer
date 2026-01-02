package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
public class PlayerGetsBallEvent extends GameEvent {

    Team team;
    Player player;

}
