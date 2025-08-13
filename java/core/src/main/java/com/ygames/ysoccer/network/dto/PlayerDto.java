package com.ygames.ysoccer.network.dto;

public class PlayerDto {

    public String name;
    public DataDto currentDataDto;

    public PlayerDto() {
    }

    public PlayerDto(String name, DataDto currentDataDto) {
        this.name = name;
        this.currentDataDto = currentDataDto;
    }
}
