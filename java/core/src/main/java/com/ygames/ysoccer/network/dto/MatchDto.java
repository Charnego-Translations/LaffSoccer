package com.ygames.ysoccer.network.dto;

public class MatchDto {
    public MatchStateDto stateDto;
    public MatchSettingsDto matchSettingsDto;
    public BallDto ballDto;
    public TeamDto[] teamDto;
    public int rank;

    public MatchDto() {
    }
}
