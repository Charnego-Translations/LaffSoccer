package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Skin;

public class PlayerDto {

    public String name;
    public String shirtName;
    public Player.Role role;
    public int number;
    public Skin.Color skinColor;
    public Hair.Color hairColor;
    public FrameDataDto currentDataDto;

    public PlayerDto() {
    }
}
