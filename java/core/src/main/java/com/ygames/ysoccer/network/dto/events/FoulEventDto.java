package com.ygames.ysoccer.network.dto.events;

import com.ygames.ysoccer.network.dto.FoulDto;

public class FoulEventDto {

    public FoulDto foulDto;

    public FoulEventDto() {
    }

    public FoulEventDto(FoulDto foulDto) {
        this.foulDto = foulDto;
    }
}
