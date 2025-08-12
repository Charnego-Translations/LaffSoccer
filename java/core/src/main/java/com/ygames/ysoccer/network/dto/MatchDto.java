package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.network.dto.mappers.MatchSettingsMapper;

public class MatchDto {
    MatchSettingsDto matchSettingsDto;
    BallDto ballDto;

    public MatchDto() {
    }

    public MatchDto(Match match) {
        this.matchSettingsDto = MatchSettingsMapper.toDto(match.getSettings());
    }

    public MatchSettingsDto getMatchSettingsDto() {
        return matchSettingsDto;
    }
}
