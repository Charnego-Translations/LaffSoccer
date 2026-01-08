package com.ygames.ysoccer.events;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.framework.SoundManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CrowdChantsEvent extends GameEvent {

    public Sound sound;

    public CrowdChantsEvent() {
        this.sound = SoundManager.getSound(SoundManager.SoundClass.CHANTS);
    }
}
