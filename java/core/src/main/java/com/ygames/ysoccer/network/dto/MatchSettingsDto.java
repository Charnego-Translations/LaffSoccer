package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;

public class MatchSettingsDto {

    public SceneSettings.Time time;
    public int darkShadow;
    public Pitch.Type pitchType;
    public int windSpeed;
    public int sky;
    public float shadowAlpha;
    public boolean radar;

    public MatchSettingsDto() {
    }
}
