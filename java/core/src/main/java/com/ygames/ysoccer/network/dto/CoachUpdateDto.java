package com.ygames.ysoccer.network.dto;

public class CoachUpdateDto {

    public float x;
    public float y;
    public int fmx;

    public CoachUpdateDto() {
    }

    public CoachUpdateDto(float x, float y, int fmx) {
        this.x = x;
        this.y = y;
        this.fmx = fmx;
    }
}
