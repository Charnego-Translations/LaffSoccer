package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Goal;

public class GoalDto {
    public int playerIndex;
    public int playerTeamIndex;
    public int minute;
    public Goal.Type type;
}
