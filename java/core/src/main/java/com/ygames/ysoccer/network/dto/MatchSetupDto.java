package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.MatchSettings;

public class MatchSetupDto {

    public MatchSettingsDto matchSettingsDto;

    public MatchSetupDto() {
    }

    public MatchSetupDto(MatchSettingsDto matchSettingsDto) {
        this.matchSettingsDto = matchSettingsDto;
    }

    public static MatchSetupDto toDto(MatchSettings matchSettings) {
        return new MatchSetupDto(
            MatchSettingsDto.toDto(matchSettings)
        );
    }
}
