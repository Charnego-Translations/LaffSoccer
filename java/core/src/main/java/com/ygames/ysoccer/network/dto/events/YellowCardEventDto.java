package com.ygames.ysoccer.network.dto.events;

public class YellowCardEventDto {

    public int teamIndex;
    public int lineupIndex;

    public YellowCardEventDto() {
    }

    public YellowCardEventDto(int teamIndex, int lineupIndex) {
        this.teamIndex = teamIndex;
        this.lineupIndex = lineupIndex;
    }
}
