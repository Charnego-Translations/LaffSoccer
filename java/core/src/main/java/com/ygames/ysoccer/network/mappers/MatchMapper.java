package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.TeamDto;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchMapper {
    public static MatchDto toDto(Match match) {
        MatchDto matchDto = new MatchDto();
        matchDto.matchSettingsDto = MatchSettingsMapper.toDto(match.getSettings());
        matchDto.ballDto = BallMapper.toDto(match.getBall());
        matchDto.teamDto = new TeamDto[2];
        matchDto.teamDto[HOME] = TeamMapper.toDto(match.team[HOME]);
        matchDto.teamDto[AWAY] = TeamMapper.toDto(match.team[AWAY]);
        matchDto.rank = match.rank;
        matchDto.displayControlledPlayer = match.displayControlledPlayer;
        return matchDto;
    }

    public static Match fromDto(MatchDto matchDto) {
        Match match = new Match();
        MatchSettings matchSettings = MatchSettingsMapper.fromDto(matchDto.matchSettingsDto);
        match.setSettings(matchSettings);
        match.setBall(BallMapper.fromDto(matchDto.ballDto, matchSettings));
        match.setTeam(HOME, TeamMapper.fromDto(matchDto.teamDto[HOME]));
        match.setTeam(AWAY, TeamMapper.fromDto(matchDto.teamDto[AWAY]));
        match.rank = matchDto.rank;
        match.displayControlledPlayer = matchDto.displayControlledPlayer;
        return match;
    }
}
