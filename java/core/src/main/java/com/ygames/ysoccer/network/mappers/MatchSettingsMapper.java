package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;

public class MatchSettingsMapper {

    public static MatchSettingsDto toDto(MatchSettings matchSettings) {
        MatchSettingsDto dto = new MatchSettingsDto();
        dto.time = matchSettings.time;
        dto.pitchType = matchSettings.pitchType;
        dto.windSpeed = matchSettings.wind.speed;
        dto.windDirection = matchSettings.wind.direction;
        dto.sky = matchSettings.sky;
        dto.shadowAlpha = matchSettings.shadowAlpha;
        dto.radar = matchSettings.radar;
        return dto;
    }

    public static MatchSettings fromDto(MatchSettingsDto dto) {
        MatchSettings matchSettings = new MatchSettings();
        matchSettings.time = dto.time;
        matchSettings.pitchType = dto.pitchType;
        matchSettings.wind.speed = dto.windSpeed;
        matchSettings.wind.direction = dto.windDirection;
        matchSettings.sky = dto.sky;
        matchSettings.shadowAlpha = dto.shadowAlpha;
        matchSettings.radar = dto.radar;
        return matchSettings;
    }
}
