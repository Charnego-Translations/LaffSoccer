package com.ygames.ysoccer.match;

public class Grass {

    float friction;
    float bounce;

    Grass() {
    }

    Grass(float friction, float bounce) {
        this.friction = friction;
        this.bounce = bounce;
    }

    void copy(Grass other) {
        this.friction = other.friction;
        this.bounce = other.bounce;
    }
}
