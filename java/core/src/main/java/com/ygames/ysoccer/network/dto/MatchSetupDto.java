package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.network.dto.mappers.MatchMapper;

public class MatchSetupDto {

    public MatchSettingsDto matchSettingsDto;
    public MatchDto matchDto;

    public MatchSetupDto() {
    }

    public MatchSetupDto(MatchDto matchDto) {
        this.matchDto = matchDto;
    }

    public static MatchSetupDto toDto(Match match) {
        return new MatchSetupDto(MatchMapper.toDto(match));
    }
}
