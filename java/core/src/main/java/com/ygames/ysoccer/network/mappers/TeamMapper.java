package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.network.dto.TeamDto;

public class TeamMapper {

    public static TeamDto toDto(Team team) {
        TeamDto teamDto = new TeamDto();
        teamDto.name = team.name;
        return teamDto;
    }

    public static Team fromDto(TeamDto teamDto) {
        Team team = new Team();
        team.name = teamDto.name;
        return team;
    }
}
