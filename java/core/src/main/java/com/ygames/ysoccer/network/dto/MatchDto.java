package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;

public class MatchDto {
    public MatchDto() {
    }

    public static MatchDto toDto(Match match) {
        return new MatchDto();
    }

    public static Match fromDto(MatchDto matchDto) {
        return new Match();
    }
}
