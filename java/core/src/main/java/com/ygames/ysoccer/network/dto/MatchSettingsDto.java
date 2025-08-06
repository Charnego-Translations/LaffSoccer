package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;

public class MatchSettingsDto {

    public SceneSettings.Time time;
    public Pitch.Type pitchType;
    public int sky;

    public MatchSettingsDto() {
    }

    public MatchSettingsDto(MatchSettings matchSettings) {
        this.time = matchSettings.time;
        this.pitchType = matchSettings.pitchType;
        this.sky = matchSettings.sky;
    }

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
