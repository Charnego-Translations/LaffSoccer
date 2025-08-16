package com.ygames.ysoccer.network.dto;

public class MatchDto {
    public MatchSettingsDto matchSettingsDto;
    public BallDto ballDto;
    public TeamDto[] teamDto;
    public int rank;
    public boolean displayControlledPlayer;
    public boolean displayFoulMaker;
    public boolean displayBallOwner;

    public MatchDto() {
    }
}
