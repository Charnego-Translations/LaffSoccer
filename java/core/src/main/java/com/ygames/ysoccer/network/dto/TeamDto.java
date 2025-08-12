package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Kit;

import java.util.List;

public class TeamDto {
    public String name;
    public List<Kit> kits;
    public List<PlayerDto> lineup;

    public TeamDto() {
    }
}
