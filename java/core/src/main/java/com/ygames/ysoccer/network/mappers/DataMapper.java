package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.FrameData;
import com.ygames.ysoccer.network.dto.FrameDataDto;

public class DataMapper {

    public static FrameDataDto toDto (FrameData data) {
        return new FrameDataDto(data.x, data.y, data.z, data.fmx, data.fmy, data.isVisible, data.isHumanControlled);
    }

    public static FrameData fromDto(FrameDataDto dto) {
        return new FrameData(dto.x, dto.y, dto.z, dto.fmx, dto.fmy, dto.isVisible, dto.isHumanControlled, false, 0, 0, 0);
    }
}
