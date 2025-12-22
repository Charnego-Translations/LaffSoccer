package com.ygames.ysoccer.network.dto;

public class KitDto {

    public String style;
    public int shirt1;
    public int shirt2;
    public int shirt3;
    public int shorts;
    public int socks;

    public KitDto() {
    }

    public KitDto(String style, int shirt1, int shirt2, int shirt3, int shorts, int socks) {
        this.style = style;
        this.shirt1 = shirt1;
        this.shirt2 = shirt2;
        this.shirt3 = shirt3;
        this.shorts = shorts;
        this.socks = socks;
    }
}
