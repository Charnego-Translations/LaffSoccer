package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;

public class MatchSettingsDto {

    public SceneSettings.Time time;
    public Pitch.Type pitchType;
    public int sky;
    public boolean radar;

    public MatchSettingsDto() {
    }

    public MatchSettingsDto(SceneSettings.Time time, Pitch.Type pitchType, int sky, boolean radar) {
        this.time = time;
        this.pitchType = pitchType;
        this.sky = sky;
        this.radar = radar;
    }
}
