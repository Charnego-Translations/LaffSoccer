package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Coach;
import com.ygames.ysoccer.network.dto.CoachDto;
import com.ygames.ysoccer.network.dto.CoachUpdateDto;

public class CoachMapper {

    public static CoachDto toDto(Coach coach) {
        return new CoachDto(coach.name, coach.teamIndex, coach.x, coach.y, coach.fmx);
    }

    public static CoachUpdateDto toUpdateDto(Coach coach) {
        return new CoachUpdateDto(coach.x, coach.y, coach.fmx);
    }

    public static Coach fromDto(CoachDto dto) {
        return new Coach(dto.name, "", null, dto.teamIndex, 0, dto.x, dto.y, dto.fmx);
    }

    public static void updateFromDto(Coach coach, CoachUpdateDto updateDto) {
        coach.x = updateDto.x;
        coach.y = updateDto.y;
        coach.fmx = updateDto.fmx;
    }
}
