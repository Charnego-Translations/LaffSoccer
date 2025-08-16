package com.ygames.ysoccer.network.dto;

public class CoachDto {
    public String name;
    public int teamIndex;
    public float x;
    public float y;
    public int fmx;

    public CoachDto() {
    }

    public CoachDto(String name, int teamIndex, float x, float y, int fmx) {
        this.name = name;
        this.teamIndex = teamIndex;
        this.x = x;
        this.y = y;
        this.fmx = fmx;
    }
}
