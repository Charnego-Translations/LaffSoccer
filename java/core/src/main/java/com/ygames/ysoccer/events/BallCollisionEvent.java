package com.ygames.ysoccer.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BallCollisionEvent extends GameEvent {
    public float strength;
}
