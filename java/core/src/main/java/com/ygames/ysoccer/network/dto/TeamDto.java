package com.ygames.ysoccer.network.dto;

import java.util.List;

public class TeamDto {
    public String name;
    public CoachDto coachDto;
    public List<KitDto> kits;
    public List<PlayerDto> lineup;

    public TeamDto() {
    }
}
