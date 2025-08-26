package com.ygames.ysoccer.events;

public class CrowdChantsEvent extends GameEvent {

    public boolean enabled;

    public CrowdChantsEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
