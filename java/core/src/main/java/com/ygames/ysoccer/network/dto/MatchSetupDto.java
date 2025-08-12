package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.mappers.MatchSettingsMapper;

public class MatchSetupDto {

    public MatchSettingsDto matchSettingsDto;
    public MatchDto matchDto;

    public MatchSetupDto() {
    }

    public MatchSetupDto(MatchSettingsDto matchSettingsDto, MatchDto matchDto) {
        this.matchSettingsDto = matchSettingsDto;
        this.matchDto = matchDto;
    }

    public static MatchSetupDto toDto(MatchSettings matchSettings, Match match) {
        return new MatchSetupDto(
            MatchSettingsMapper.toDto(matchSettings),
            MatchDto.toDto(match)
        );
    }
}
