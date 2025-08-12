package com.ygames.ysoccer.network.mappers;

import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.network.dto.KitDto;

public class KitMapper {

    public static KitDto toDto(Kit kit) {
        return new KitDto(kit.style, kit.shirt1, kit.shirt2, kit.shirt3, kit.shorts, kit.socks);
    }

    public static Kit fromDto(KitDto dto) {
        return new Kit(dto.style, dto.shirt1, dto.shirt2, dto.shirt3, dto.shorts, dto.socks);
    }
}
