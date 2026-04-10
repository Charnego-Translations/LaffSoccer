package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Team;

import java.util.List;

public class TeamDto {

    public String name;
    public Team.Type type;
    public String city;
    public String stadium;
    public CoachDto coachDto;
    public List<KitDto> kits;
    public List<PlayerDto> lineup;

    public TeamDto() {
    }
}
