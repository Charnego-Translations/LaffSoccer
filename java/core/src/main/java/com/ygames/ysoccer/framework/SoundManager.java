package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.events.BallBounceEvent;
import com.ygames.ysoccer.events.BallCollisionEvent;
import com.ygames.ysoccer.events.BallKickEvent;
import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.events.CrowdChantsEvent;
import com.ygames.ysoccer.events.HomeGoalEvent;
import com.ygames.ysoccer.events.KeeperDeflectEvent;
import com.ygames.ysoccer.events.KeeperHoldEvent;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.events.WhistleEvent;

public class SoundManager {
    public SoundManager() {
        EventManager.subscribe(BallBounceEvent.class, ballBounceEvent -> {
            Assets.Sounds.bounce.play(Math.min(ballBounceEvent.speed / 250, 1) * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(BallCollisionEvent.class, ballCollisionEvent -> {
            Assets.Sounds.post.play(ballCollisionEvent.strength * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(BallKickEvent.class, ballKickEvent -> {
            Assets.Sounds.kick.play(ballKickEvent.strength * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(CelebrationEvent.class, celebrationEvent -> {
            Assets.Sounds.celebration.play(Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(CrowdChantsEvent.class, crowdChantsEvent -> {
            Assets.Sounds.chant.play(crowdChantsEvent.enabled ? Assets.Sounds.volume / 100f : 0);
        });

        EventManager.subscribe(HomeGoalEvent.class, homeGoalEvent -> {
            Assets.Sounds.homeGoal.play(Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(KeeperDeflectEvent.class, keeperDeflectEvent -> {
            Assets.Sounds.deflect.play(0.5f * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(KeeperHoldEvent.class, keeperHoldEvent -> {
            Assets.Sounds.hold.play(0.5f * Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
            Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
        });

        EventManager.subscribe(PeriodStopEvent.class, periodStopEvent -> {
            Assets.Sounds.end.play(Assets.Sounds.volume / 100f);
        });

        EventManager.subscribe(WhistleEvent.class, whistleEvent -> {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);
        });
    }

    public static void setIntroVolume() {
        Assets.Sounds.intro.setVolume(Assets.Sounds.introId, Assets.Sounds.volume / 100f);
    }

    public static void setCrowdVolume() {
        Assets.Sounds.crowd.setVolume(Assets.Sounds.crowdId, Assets.Sounds.volume / 100f);
    }

    public static void stopMatchSounds() {
        Assets.Sounds.chant.stop();
        Assets.Sounds.crowd.stop();
        Assets.Sounds.end.stop();
        Assets.Sounds.homeGoal.stop();
        Assets.Sounds.intro.stop();
        Assets.Commentary.stop();
    }
}
