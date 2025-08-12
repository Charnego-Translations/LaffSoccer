package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.network.dto.mappers.BallMapper;
import com.ygames.ysoccer.network.dto.mappers.MatchSettingsMapper;

public class MatchDto {
    MatchSettingsDto matchSettingsDto;
    BallDto ballDto;
    TeamDto[] teamDto;

    public MatchDto() {
    }

    public MatchDto(Match match) {
        this.matchSettingsDto = MatchSettingsMapper.toDto(match.getSettings());
        this.ballDto = BallMapper.toDto(match.getBall());
    }

    public MatchSettingsDto getMatchSettingsDto() {
        return matchSettingsDto;
    }

    public BallDto getBallDto() {
        return ballDto;
    }
}
