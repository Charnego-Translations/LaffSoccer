package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.FrameData;
import com.ygames.ysoccer.network.dto.FrameDataDto;

public class FrameDataMapper {

    public static FrameDataDto toDto(FrameData data) {
        return new FrameDataDto(data.x, data.y, data.z, data.fmx, data.fmy, data.isVisible, data.isHumanControlled);
    }

    public static FrameDataDto toUpdateDto(FrameData data) {
        return toDto(data);
    }

    public static FrameData fromDto(FrameDataDto dto) {
        return new FrameData(dto.x, dto.y, dto.z, dto.fmx, dto.fmy, dto.isVisible, dto.isHumanControlled, false, 0, 0, 0);
    }

    public static void updateFromDto(FrameData currentData, FrameDataDto currentDataDto) {
        currentData.x = currentDataDto.x;
        currentData.y = currentDataDto.y;
        currentData.z = currentDataDto.z;
        currentData.fmx = currentDataDto.fmx;
        currentData.fmy = currentDataDto.fmy;
        currentData.isVisible = currentDataDto.isVisible;
        currentData.isHumanControlled = currentDataDto.isHumanControlled;
    }
}
