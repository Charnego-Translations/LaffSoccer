package com.ygames.ysoccer.network.dto.mappers;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.TeamDto;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchMapper {
    public static MatchDto toDto(Match match) {
        MatchDto matchDto = new MatchDto();
        matchDto.setMatchSettingsDto(MatchSettingsMapper.toDto(match.getSettings()));
        matchDto.setBallDto(BallMapper.toDto(match.getBall()));
        TeamDto[] teamDto = new TeamDto[2];
        teamDto[0] = TeamMapper.toDto(match.team[0]);
        teamDto[1] = TeamMapper.toDto(match.team[1]);
        matchDto.setTeamDto(teamDto);
        return matchDto;
    }

    public static Match fromDto(MatchDto matchDto) {
        Match match = new Match();
        MatchSettings matchSettings = MatchSettingsMapper.fromDto(matchDto.getMatchSettingsDto());
        match.setSettings(matchSettings);
        match.setBall(BallMapper.fromDto(matchDto.getBallDto(), matchSettings));
        TeamDto[] teamDto = matchDto.getTeamDto();
        match.setTeam(HOME, TeamMapper.fromDto(teamDto[HOME]));
        match.setTeam(AWAY, TeamMapper.fromDto(teamDto[AWAY]));
        return match;
    }
}
