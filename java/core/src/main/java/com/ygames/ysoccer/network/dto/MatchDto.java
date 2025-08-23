package com.ygames.ysoccer.network.dto;

public class MatchDto {
    public MatchSettingsDto matchSettingsDto;
    public BallDto ballDto;
    public TeamDto[] teamDto;
    public int rank;
    public boolean displayControlledPlayer;
    public boolean displayFoulMaker;
    public boolean displayBallOwner;
    public boolean displayTime;
    public boolean displayRadar;
    public boolean displayWindVane;
    public boolean displayRosters;
    public boolean displayScore;
    public boolean displayPenaltiesScore;
    public boolean displayStatistics;
    public boolean displayGoalScorer;
    public boolean displayBenchPlayers;
    public boolean displayBenchFormation;
    public boolean displayTacticsSwitch;
    public boolean displayHelp;
    public boolean displayPause;
    public boolean displayReplayGui;

    public MatchDto() {
    }
}
