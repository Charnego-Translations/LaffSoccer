package com.ygames.ysoccer.network.dto.mappers;

import com.ygames.ysoccer.match.Ball;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.BallDto;

public class BallMapper {

    public static BallDto toDto(Ball ball) {
        BallDto ballDto = new BallDto();
        ballDto.setX(ball.getX());
        ballDto.setY(ball.getY());
        ballDto.setZ(ball.getZ());
        return ballDto;
    }

    public static Ball fromDto(BallDto ballDto, SceneSettings sceneSettings) {
        return new Ball(sceneSettings);
    }
}
