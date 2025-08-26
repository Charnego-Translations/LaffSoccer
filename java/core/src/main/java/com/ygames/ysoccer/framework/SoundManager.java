package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.events.BallBounceEvent;
import com.ygames.ysoccer.events.BallKickEvent;
import com.ygames.ysoccer.events.CrowdChantsEvent;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.events.WhistleEvent;

public class SoundManager {
    public SoundManager() {
        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
        });

        EventManager.subscribe(WhistleEvent.class, whistleEvent -> {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(BallKickEvent.class, ballKickEvent -> {
            Assets.Sounds.kick.play(ballKickEvent.strength * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(CrowdChantsEvent.class, crowdChantsEvent -> {
            Assets.Sounds.chant.play(crowdChantsEvent.enabled ? Assets.Sounds.volume / 100f : 0);
        });

        EventManager.subscribe(BallBounceEvent.class, ballBounceEvent -> {
            Assets.Sounds.bounce.play(Math.min(ballBounceEvent.speed / 250, 1) * Assets.Sounds.volume / 100f);
        });
    }
}
