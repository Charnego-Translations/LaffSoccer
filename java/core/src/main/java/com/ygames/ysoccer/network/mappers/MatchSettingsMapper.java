package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;

public class MatchSettingsMapper {

    public static MatchSettingsDto toDto(MatchSettings settings) {
        return new MatchSettingsDto(settings.time, settings.pitchType, settings.sky, settings.radar);
    }

    public static MatchSettings fromDto(MatchSettingsDto dto) {
        MatchSettings matchSettings = new MatchSettings();
        matchSettings.time = dto.time;
        matchSettings.pitchType = dto.pitchType;
        matchSettings.sky = dto.sky;
        matchSettings.radar = dto.radar;
        return matchSettings;
    }
}
