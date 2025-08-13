package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Data;
import com.ygames.ysoccer.network.dto.DataDto;

public class DataMapper {

    public static DataDto toDto (Data data) {
        return new DataDto(data.x, data.y, data.z, data.fmx, data.fmy, data.isVisible, data.isHumanControlled);
    }

    public static Data fromDto(DataDto dto) {
        return new Data(dto.x, dto.y, dto.z, dto.fmx, dto.fmy, dto.isVisible, dto.isHumanControlled, false, 0, 0, 0);
    }
}
