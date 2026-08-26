package com.ygames.ysoccer.network.dto.events;

public class PlayerSwapEventDto {

    public int teamIndex;
    public int lineupIndexA;
    public int lineupIndexB;

    public PlayerSwapEventDto() {
    }

    public PlayerSwapEventDto(int teamIndex, int lineupIndexA, int lineupIndexB) {
        this.teamIndex = teamIndex;
        this.lineupIndexA = lineupIndexA;
        this.lineupIndexB = lineupIndexB;
    }
}
