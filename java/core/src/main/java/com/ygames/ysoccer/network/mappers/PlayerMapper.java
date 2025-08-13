package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.PlayerDto;

public class PlayerMapper {

    public static PlayerDto toDto(Player player) {
        return new PlayerDto(player.name, DataMapper.toDto(player.currentData));
    }

    public static Player fromDto(PlayerDto playerDto) {
        Player player = new Player();
        player.name = playerDto.name;
        player.currentData = DataMapper.fromDto(playerDto.currentDataDto);
        return player;
    }
}
