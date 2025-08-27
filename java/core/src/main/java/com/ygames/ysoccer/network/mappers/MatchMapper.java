package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.dto.TeamDto;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchMapper {

    public static MatchDto toDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.matchSettingsDto = MatchSettingsMapper.toDto(match.getSettings());
        dto.light = match.light;
        dto.ballDto = BallMapper.toDto(match.getBall());
        dto.teamDto = new TeamDto[2];
        dto.teamDto[HOME] = TeamMapper.toDto(match.team[HOME]);
        dto.teamDto[AWAY] = TeamMapper.toDto(match.team[AWAY]);
        dto.competitionDto = CompetitionMapper.toDto(match.competition);
        dto.rank = match.rank;
        dto.displayControlledPlayer = match.displayControlledPlayer;
        dto.displayFoulMaker = match.displayFoulMaker;
        dto.displayBallOwner = match.displayBallOwner;
        dto.displayTime = match.displayTime;
        dto.displayRadar = match.displayRadar;
        dto.displayWindVane = match.displayWindVane;
        dto.displayRosters = match.displayRosters;
        dto.displayScore = match.displayScore;
        dto.displayPenaltiesScore = match.displayPenaltiesScore;
        dto.displayStatistics = match.displayStatistics;
        dto.displayGoalScorer = match.displayGoalScorer;
        dto.displayBenchPlayers = match.displayBenchPlayers;
        dto.displayBenchFormation = match.displayBenchFormation;
        dto.displayTacticsSwitch = match.displayTacticsSwitch;
        dto.displayHelp = match.displayHelp;
        dto.displayPause = match.displayPause;
        dto.displayReplayGui = match.displayReplayGui;
        dto.displayHighlightsGui = match.displayHighlightsGui;
        dto.displayReplayControls = match.displayReplayControls;
        return dto;
    }

    public static MatchUpdateDto toUpdateDto(Match match) {
        MatchUpdateDto dto = new MatchUpdateDto();
        dto.light = match.light;
        dto.displayRosters = match.displayRosters;
        return dto;
    }

    public static Match fromDto(MatchDto dto) {
        Match match = new Match();
        MatchSettings matchSettings = MatchSettingsMapper.fromDto(dto.matchSettingsDto);
        match.setSettings(matchSettings);
        match.light = dto.light;
        match.setBall(BallMapper.fromDto(dto.ballDto, matchSettings));
        match.setTeam(HOME, TeamMapper.fromDto(dto.teamDto[HOME]));
        match.setTeam(AWAY, TeamMapper.fromDto(dto.teamDto[AWAY]));
        match.competition = CompetitionMapper.fromDto(dto.competitionDto);
        match.rank = dto.rank;
        match.displayControlledPlayer = dto.displayControlledPlayer;
        match.displayFoulMaker = dto.displayFoulMaker;
        match.displayBallOwner = dto.displayBallOwner;
        match.displayTime = dto.displayTime;
        match.displayRadar = dto.displayRadar;
        match.displayWindVane = dto.displayWindVane;
        match.displayRosters = dto.displayRosters;
        match.displayScore = dto.displayScore;
        match.displayPenaltiesScore = dto.displayPenaltiesScore;
        match.displayStatistics = dto.displayStatistics;
        match.displayGoalScorer = dto.displayGoalScorer;
        match.displayBenchPlayers = dto.displayBenchPlayers;
        match.displayBenchFormation = dto.displayBenchFormation;
        match.displayTacticsSwitch = dto.displayTacticsSwitch;
        match.displayHelp = dto.displayHelp;
        match.displayPause = dto.displayPause;
        match.displayReplayGui = dto.displayReplayGui;
        match.displayHighlightsGui = dto.displayHighlightsGui;
        match.displayReplayControls = dto.displayReplayControls;
        return match;
    }

    public static void updateFromDto(Match match, MatchUpdateDto dto) {
        match.light = dto.light;
        match.displayRosters = dto.displayRosters;
    }
}
