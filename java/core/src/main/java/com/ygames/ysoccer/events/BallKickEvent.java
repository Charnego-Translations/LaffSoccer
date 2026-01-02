package com.ygames.ysoccer.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BallKickEvent extends GameEvent {

    public float strength;
    public boolean isSuperShoot;

    public BallKickEvent(float strength) {
        this(strength, false);
    }

}
