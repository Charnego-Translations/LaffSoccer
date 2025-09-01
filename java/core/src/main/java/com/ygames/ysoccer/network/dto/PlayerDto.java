package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Skin;

public class PlayerDto {

    public String name;
    public String shirtName;
    public int number;
    public Skin.Color skinColor;
    public FrameDataDto currentDataDto;

    public PlayerDto() {
    }
}
