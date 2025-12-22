package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.network.dto.CompetitionDto;

public class CompetitionMapper {

    public static CompetitionDto toDto(Competition competition) {
        CompetitionDto dto = new CompetitionDto();
        dto.name = competition.name;
        return dto;
    }

    public static Competition fromDto(CompetitionDto dto) {
        Friendly friendly = new Friendly();
        friendly.name = dto.name;
        return friendly;
    }
}
