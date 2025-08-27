package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.PlayerDto;

public class PlayerMapper {

    public static PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.name = player.name;
        dto.shirtName = player.shirtName;
        dto.number = player.number;
        dto.currentDataDto = FrameDataMapper.toDto(player.currentData);
        return dto;
    }

    public static Player fromDto(PlayerDto dto) {
        Player player = new Player();
        player.name = dto.name;
        player.shirtName = dto.shirtName;
        player.number = dto.number;
        player.currentData = FrameDataMapper.fromDto(dto.currentDataDto);
        return player;
    }
}
