package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.match.Ball;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.BallDto;
import com.ygames.ysoccer.network.dto.BallUpdateDto;

import static com.ygames.ysoccer.framework.GLGame.SERVER_UPDATES_PER_SECOND;

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
        // update ball speed and angle for camera
        float ballDist = EMath.dist(dto.currentDataDto.x, dto.currentDataDto.y, ball.currentData.x, ball.currentData.y);
        if (ballDist < SERVER_UPDATES_PER_SECOND) {
            ball.setV(ballDist * SERVER_UPDATES_PER_SECOND);
            ball.setA(EMath.aTan2(dto.currentDataDto.y - ball.currentData.y, dto.currentDataDto.x - ball.currentData.x));
        }

        FrameDataMapper.updateFromDto(ball.currentData, dto.currentDataDto);
        ball.ownerIndex = dto.ownerIndex;
        ball.ownerTeamIndex = dto.ownerTeamIndex;
    }
}
