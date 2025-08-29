package com.ygames.ysoccer.network.dto.events;

public class BallCollisionEventDto {

    public float strength;

    public BallCollisionEventDto() {
    }

    public BallCollisionEventDto(float strength) {
        this.strength = strength;
    }
}
