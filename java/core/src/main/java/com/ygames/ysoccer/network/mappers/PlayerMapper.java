package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.PlayerDto;

public class PlayerMapper {

    public static PlayerDto toDto(Player player) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.name = player.name;
        return playerDto;
    }

    public static Player fromDto(PlayerDto playerDto) {
        Player player = new Player();
        player.name = playerDto.name;
        return player;
    }
}
