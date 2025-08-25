package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.events.MatchIntroEvent;

public class SoundManager {
    public SoundManager() {
        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
        });
    }
}
