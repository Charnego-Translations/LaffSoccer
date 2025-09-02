package com.ygames.ysoccer.network.dto;

import java.util.List;

public class TeamUpdateDto {

    public CoachUpdateDto coachUpdateDto;
    public List<PlayerUpdateDto> lineup;

    public TeamUpdateDto() {
    }
}
