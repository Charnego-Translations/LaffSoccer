package com.ygames.ysoccer.network.dto;

public class PlayerDto {

    public String name;
    public FrameDataDto currentDataDto;

    public PlayerDto() {
    }

    public PlayerDto(String name, FrameDataDto currentDataDto) {
        this.name = name;
        this.currentDataDto = currentDataDto;
    }
}
