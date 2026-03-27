package com.ygames.ysoccer.network.dto;

public class FoulDto {

    public float positionX;
    public float positionY;
    public int playerIndex;
    public int playerTeamIndex;

    public FoulDto() {
    }

    public FoulDto(float positionX, float positionY, int playerIndex, int playerTeamIndex) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.playerIndex = playerIndex;
        this.playerTeamIndex = playerTeamIndex;
    }
}
