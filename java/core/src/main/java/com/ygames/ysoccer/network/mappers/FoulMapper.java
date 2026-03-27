package com.ygames.ysoccer.network.mappers;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.FoulDto;

public class FoulMapper {

    public static FoulDto toDto(Match.Foul foul) {
        return new FoulDto(foul.position.x, foul.position.y, foul.player.lineupIndex(), foul.player.team.index);
    }

    public static Match.Foul fromDto(Match match, FoulDto dto) {
        Player player = match.team[dto.playerTeamIndex].lineup.get(dto.playerIndex);
        Match.Foul foul = new Match.Foul();
        foul.position = new Vector2(dto.positionX, dto.positionY);
        foul.player = player;
        return foul;
    }
}
