package com.ygames.ysoccer.events;

public class BallKickEvent extends GameEvent {

    public float strength;

    public BallKickEvent(float strength) {
        this.strength = strength;
    }
}
