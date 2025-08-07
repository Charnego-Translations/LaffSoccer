package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;

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
            MatchSettingsDto.toDto(matchSettings),
            MatchDto.toDto(match)
        );
    }
}
