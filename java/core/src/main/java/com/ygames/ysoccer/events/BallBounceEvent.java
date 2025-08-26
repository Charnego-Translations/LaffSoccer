package com.ygames.ysoccer.events;

public class BallBounceEvent extends GameEvent {

    public float speed;

    public BallBounceEvent(float speed) {
        this.speed = speed;
    }
}
