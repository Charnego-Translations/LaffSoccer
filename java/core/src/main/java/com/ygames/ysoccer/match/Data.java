package com.ygames.ysoccer.match;

public class Data {
    public int x;
    public int y;
    public int z;
    public int fmx;
    public int fmy;
    public boolean isVisible;
    public boolean isHumanControlled;
    public boolean isBestDefender;
    public int frameDistance;
    public int playerState;
    public int playerAiState;

    public Data() {
    }

    public Data(int x, int y, int z, int fmx, int fmy, boolean isVisible, boolean isHumanControlled, boolean isBestDefender, int frameDistance, int playerState, int playerAiState) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fmx = fmx;
        this.fmy = fmy;
        this.isVisible = isVisible;
        this.isHumanControlled = isHumanControlled;
        this.isBestDefender = isBestDefender;
        this.frameDistance = frameDistance;
        this.playerState = playerState;
        this.playerAiState = playerAiState;
    }
}
