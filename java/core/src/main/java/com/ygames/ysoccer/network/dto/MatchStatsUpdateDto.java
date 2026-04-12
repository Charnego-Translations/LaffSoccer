package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.MatchStats;

public class MatchStatsUpdateDto {

    public MatchStats[] stats;

    public MatchStatsUpdateDto() {
    }

    public MatchStatsUpdateDto(MatchStats[] stats) {
        this.stats = stats;
    }
}
