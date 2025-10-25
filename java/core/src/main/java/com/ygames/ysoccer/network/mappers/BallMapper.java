package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Ball;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.BallDto;
import com.ygames.ysoccer.network.dto.BallUpdateDto;

public class BallMapper {

    public static BallDto toDto(Ball ball) {
        BallDto dto = new BallDto();
        dto.x = ball.getX();
        dto.y = ball.getY();
        dto.z = ball.getZ();
        dto.currentDataDto = FrameDataMapper.toDto(ball.currentData);
        return dto;
    }

    public static BallUpdateDto toUpdateDto(Ball ball) {
        BallUpdateDto dto = new BallUpdateDto();
        dto.currentDataDto = FrameDataMapper.toUpdateDto(ball.currentData);
        dto.ownerIndex = ball.ownerIndex;
        dto.ownerTeamIndex = ball.ownerTeamIndex;
        return dto;
    }

    public static Ball fromDto(BallDto ballDto, SceneSettings sceneSettings) {
        Ball ball = new Ball(sceneSettings);
        ball.setX(ballDto.x);
        ball.setY(ballDto.y);
        ball.setZ(ballDto.z);
        ball.currentData = FrameDataMapper.fromDto(ballDto.currentDataDto);
        return ball;
    }

    public static void updateFromDto(Ball ball, BallUpdateDto dto) {
        FrameDataMapper.updateFromDto(ball.currentData, dto.currentDataDto);
        ball.ownerIndex= dto.ownerIndex;
        ball.ownerTeamIndex = dto.ownerTeamIndex;
    }
}
