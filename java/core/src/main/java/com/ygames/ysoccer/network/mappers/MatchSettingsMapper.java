package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;

public class MatchSettingsMapper {

    public static MatchSettingsDto toDto(MatchSettings matchSettings) {
        MatchSettingsDto dto = new MatchSettingsDto();
        dto.time = matchSettings.time;
        dto.darkShadow = matchSettings.grass.darkShadow;
        dto.pitchType = matchSettings.pitchType;
        dto.windSpeed = matchSettings.wind.speed;
        dto.sky = matchSettings.sky;
        dto.shadowAlpha = matchSettings.shadowAlpha;
        dto.radar = matchSettings.radar;
        return dto;
    }

    public static MatchSettings fromDto(MatchSettingsDto dto) {
        MatchSettings matchSettings = new MatchSettings();
        matchSettings.time = dto.time;
        matchSettings.grass.darkShadow = dto.darkShadow;
        matchSettings.pitchType = dto.pitchType;
        matchSettings.wind.speed = dto.windSpeed;
        matchSettings.sky = dto.sky;
        matchSettings.shadowAlpha = dto.shadowAlpha;
        matchSettings.radar = dto.radar;
        return matchSettings;
    }
}
