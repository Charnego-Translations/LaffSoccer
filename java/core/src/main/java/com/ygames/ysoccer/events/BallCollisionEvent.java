package com.ygames.ysoccer.events;

public class BallCollisionEvent extends GameEvent {

    public float strength;

    public BallCollisionEvent(float strength) {
        this.strength = strength;
    }
}
