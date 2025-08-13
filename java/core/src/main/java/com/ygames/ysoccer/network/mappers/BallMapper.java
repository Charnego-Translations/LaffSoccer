package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Ball;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.BallDto;

public class BallMapper {

    public static BallDto toDto(Ball ball) {
        BallDto ballDto = new BallDto();
        ballDto.x = ball.getX();
        ballDto.y = ball.getY();
        ballDto.z = ball.getZ();
        ballDto.currentDataDto = DataMapper.toDto(ball.currentData);
        return ballDto;
    }

    public static Ball fromDto(BallDto ballDto, SceneSettings sceneSettings) {
        Ball ball = new Ball(sceneSettings);
        ball.setX(ballDto.x);
        ball.setY(ballDto.y);
        ball.setZ(ballDto.z);
        ball.currentData = DataMapper.fromDto(ballDto.currentDataDto);
        return ball;
    }
}
