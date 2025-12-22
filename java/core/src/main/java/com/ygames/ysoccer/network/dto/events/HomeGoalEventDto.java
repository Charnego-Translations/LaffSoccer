package com.ygames.ysoccer.network.dto.events;

import com.ygames.ysoccer.network.dto.GoalDto;

public class HomeGoalEventDto {

    public GoalDto goalDto;

    public HomeGoalEventDto() {
    }

    public HomeGoalEventDto(GoalDto goalDto) {
        this.goalDto = goalDto;
    }
}
