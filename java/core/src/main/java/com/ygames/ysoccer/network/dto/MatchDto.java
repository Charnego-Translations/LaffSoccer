package com.ygames.ysoccer.network.dto;

public class MatchDto {
    MatchSettingsDto matchSettingsDto;
    BallDto ballDto;
    TeamDto[] teamDto;

    public MatchDto() {
    }

    public MatchSettingsDto getMatchSettingsDto() {
        return matchSettingsDto;
    }

    public void setMatchSettingsDto(MatchSettingsDto matchSettingsDto) {
        this.matchSettingsDto = matchSettingsDto;
    }

    public BallDto getBallDto() {
        return ballDto;
    }

    public void setBallDto(BallDto ballDto) {
        this.ballDto = ballDto;
    }

    public TeamDto[] getTeamDto() {
        return teamDto;
    }

    public void setTeamDto(TeamDto[] teamDto) {
        this.teamDto = teamDto;
    }
}
