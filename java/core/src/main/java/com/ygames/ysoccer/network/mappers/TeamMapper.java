package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.network.dto.KitDto;
import com.ygames.ysoccer.network.dto.PlayerDto;
import com.ygames.ysoccer.network.dto.TeamDto;

import java.util.ArrayList;

public class TeamMapper {

    public static TeamDto toDto(Team team) {
        TeamDto teamDto = new TeamDto();
        teamDto.name = team.name;
        teamDto.coachDto = CoachMapper.toDto(team.coach);
        teamDto.kits = new ArrayList<>();
        for (Kit kit : team.kits) {
            teamDto.kits.add(KitMapper.toDto(kit));
        }
        teamDto.lineup = new ArrayList<>();
        for (Player player : team.lineup) {
            teamDto.lineup.add(PlayerMapper.toDto(player));
        }
        return teamDto;
    }

    public static Team fromDto(TeamDto teamDto) {
        Team team = new Team();
        team.name = teamDto.name;
        team.coach = CoachMapper.fromDto(teamDto.coachDto);
        for (KitDto kitDto : teamDto.kits) {
            team.kits.add(KitMapper.fromDto(kitDto));
        }
        team.lineup = new ArrayList<>();
        for (PlayerDto playerDto : teamDto.lineup) {
            team.lineup.add(PlayerMapper.fromDto(playerDto));
        }
        return team;
    }
}
