package com.ygames.ysoccer.network.dto.mappers;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchDto;

public class MatchMapper {
    public static MatchDto toDto(Match match) {
        return new MatchDto(match);
    }

    public static Match fromDto(MatchDto matchDto) {
        Match match = new Match();
        MatchSettings matchSettings = MatchSettingsMapper.fromDto(matchDto.getMatchSettingsDto());
        match.setSettings(matchSettings);
        match.setBall(BallMapper.fromDto(matchDto.getBallDto(), matchSettings));
        return match;
    }
}
