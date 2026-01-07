package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.network.dto.KitDto;
import com.ygames.ysoccer.network.dto.PlayerDto;
import com.ygames.ysoccer.network.dto.TeamDto;
import com.ygames.ysoccer.network.dto.TeamUpdateDto;

import java.util.ArrayList;

public class TeamMapper {

    public static TeamDto toDto(Team team) {
        TeamDto dto = new TeamDto();
        dto.name = team.name;
        dto.city = team.city;
        dto.stadium = team.stadium;
        dto.coachDto = CoachMapper.toDto(team.coach);
        dto.kits = new ArrayList<>();
        for (Kit kit : team.kits) {
            dto.kits.add(KitMapper.toDto(kit));
        }
        dto.lineup = new ArrayList<>();
        for (Player player : team.lineup) {
            dto.lineup.add(PlayerMapper.toDto(player));
        }
        return dto;
    }

    public static TeamUpdateDto toUpdateDto(Team team) {
        TeamUpdateDto dto = new TeamUpdateDto();
        dto.coachUpdateDto = CoachMapper.toUpdateDto(team.coach);
        dto.lineup = new ArrayList<>();
        for (Player player : team.lineup) {
            dto.lineup.add(PlayerMapper.toUpdateDto(player));
        }
        return dto;
    }

    public static Team fromDto(TeamDto dto) {
        Team team = new Team();
        team.name = dto.name;
        team.city = dto.city;
        team.stadium = dto.stadium;
        team.year = dto.year;
        team.coach = CoachMapper.fromDto(dto.coachDto);
        for (KitDto kitDto : dto.kits) {
            team.kits.add(KitMapper.fromDto(kitDto));
        }
        team.lineup = new ArrayList<>();
        for (PlayerDto playerDto : dto.lineup) {
            Player player = PlayerMapper.fromDto(playerDto);
            player.team = team;
            team.lineup.add(player);
        }
        return team;
    }

    public static void updateFromDto(Team team, TeamUpdateDto updateDto) {
        CoachMapper.updateFromDto(team.coach, updateDto.coachUpdateDto);
        for (int i = 0; i < team.lineup.size(); i++) {
            Player player = team.lineup.get(i);
            PlayerMapper.updateFromDto(player, updateDto.lineup.get(i));
        }
    }
}
