package com.ygames.ysoccer.events;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.framework.SoundManager;

public class CrowdChantsEvent extends GameEvent {

    public Sound sound;

    public CrowdChantsEvent() {
        this.sound = SoundManager.getSound(SoundManager.SoundClass.CHANTS);
    }

    public CrowdChantsEvent(Sound sound) {
        this.sound = sound;
    }
}
