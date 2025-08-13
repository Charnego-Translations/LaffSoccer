package com.ygames.ysoccer.network.dto;

public class DataDto {

    public int x;
    public int y;
    public int z;
    public int fmx;
    public int fmy;
    public boolean isVisible;
    public boolean isHumanControlled;

    public DataDto(int x, int y, int z, int fmx, int fmy, boolean isVisible, boolean isHumanControlled) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fmx = fmx;
        this.fmy = fmy;
        this.isVisible = isVisible;
        this.isHumanControlled = isHumanControlled;
    }
}
