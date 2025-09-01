package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.PlayerDto;
import com.ygames.ysoccer.network.dto.PlayerUpdateDto;

public class PlayerMapper {

    public static PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.name = player.name;
        dto.shirtName = player.shirtName;
        dto.role = player.role;
        dto.number = player.number;
        dto.skinColor = player.skinColor;
        dto.currentDataDto = FrameDataMapper.toDto(player.currentData);
        return dto;
    }

    public static PlayerUpdateDto toUpdateDto(Player player) {
        PlayerUpdateDto dto = new PlayerUpdateDto();
        dto.currentDataDto = FrameDataMapper.toUpdateDto(player.currentData);
        return dto;
    }

    public static Player fromDto(PlayerDto dto) {
        Player player = new Player();
        player.name = dto.name;
        player.shirtName = dto.shirtName;
        player.role = dto.role;
        player.number = dto.number;
        player.skinColor = dto.skinColor;
        player.currentData = FrameDataMapper.fromDto(dto.currentDataDto);
        return player;
    }

    public static void updateFromDto(Player player, PlayerUpdateDto updateDto) {
        FrameDataMapper.updateFromDto(player.currentData, updateDto.currentDataDto);
    }
}
