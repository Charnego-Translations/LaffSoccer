package com.ygames.ysoccer.network.dto.mappers;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;

public class MatchSettingsMapper {
    public static MatchSettingsDto toDto(MatchSettings matchSettings) {
        return new MatchSettingsDto(matchSettings);
    }

    public static MatchSettings fromDto(MatchSettingsDto dto) {
        MatchSettings matchSettings = new MatchSettings();
        matchSettings.time = dto.time;
        matchSettings.pitchType = dto.pitchType;
        matchSettings.sky = dto.sky;
        return matchSettings;
    }
}
