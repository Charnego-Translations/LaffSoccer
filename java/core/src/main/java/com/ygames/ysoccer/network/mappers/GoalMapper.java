package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Goal;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.GoalDto;

public class GoalMapper {

    public static GoalDto toDto(Goal goal) {
        GoalDto dto = new GoalDto();
        dto.playerIndex = goal.player.lineupIndex();
        dto.playerTeamIndex = goal.player.team.index;
        dto.minute = goal.minute;
        dto.type = goal.type;
        return dto;
    }

    public static Goal fromDto(Match match, GoalDto dto) {
        Player player = match.team[dto.playerTeamIndex].lineup.get(dto.playerIndex);
        return new Goal(player, dto.minute, dto.type);
    }
}
