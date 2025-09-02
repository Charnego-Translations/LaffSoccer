package com.ygames.ysoccer.network.dto;

public class MatchUpdateDto {

    public int light;
    public BallUpdateDto ballUpdateDto;
    public TeamUpdateDto[] teamUpdateDto;
    public boolean displayWindVane;
    public boolean displayRosters;

    public MatchUpdateDto() {
    }
}
