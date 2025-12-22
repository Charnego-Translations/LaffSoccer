package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

public class Wind {

    public int speed; // 0=none, 1=normal, 2=strong
    public int direction; // 0=E, 1=SE, 2=S, 3=SW, 4=W, 5=NW, 6=N, 7=NE
    int angle; // 0=E, 45=SE, 90=S, 135=SW, 180=W, 225=NW, 270=N, 315=NE
    int dirX;
    int dirY;

    public void init(int speed, int direction) {
        this.speed = speed;
        this.direction = direction;
        angle = 45 * direction;
        dirX = Math.round(EMath.cos(angle));
        dirY = Math.round(EMath.sin(angle));
    }
}
