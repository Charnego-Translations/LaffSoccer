package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

    private static Sound bounce;
    private static Sound celebration;
    private static Sound chant;
    private static Sound crowd;
    private static Long crowdId;
    private static Sound deflect;
    private static Sound hold;
    private static Sound homeGoal;
    private static Sound intro;
    private static Long introId;
    private static Sound kick;
    private static Sound net;
    private static Sound post;
    private static Sound end;
    private static Sound whistle;

    public static int volume;
    public static boolean crowdChantsEnabled = true;

    public void subscribeEvents() {
        EventManager.subscribe(BallBounceEvent.class, ballBounceEvent -> {
            bounce.play(Math.min(ballBounceEvent.speed / 250, 1) * volume / 100f);
        });

        EventManager.subscribe(BallCollisionEvent.class, ballCollisionEvent -> {
            post.play(ballCollisionEvent.strength * volume / 100f);
        });

        EventManager.subscribe(BallKickEvent.class, ballKickEvent -> {
            kick.play(ballKickEvent.strength * volume / 100f);
        });

        EventManager.subscribe(CelebrationEvent.class, celebrationEvent -> {
            celebration.play(volume / 100f);
        });

        EventManager.subscribe(CrowdChantsEvent.class, crowdChantsEvent -> {
            if (crowdChantsEnabled) chant.play(volume / 100f);
        });

        EventManager.subscribe(HomeGoalEvent.class, homeGoalEvent -> {
            homeGoal.play(volume / 100f);
        });

        EventManager.subscribe(KeeperDeflectEvent.class, keeperDeflectEvent -> {
            deflect.play(0.5f * volume / 100f);
        });

        EventManager.subscribe(KeeperHoldEvent.class, keeperHoldEvent -> {
            hold.play(0.5f * volume / 100f);
        });

        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            introId = intro.play(volume / 100f);
            crowdId = crowd.play(volume / 100f);
            crowd.setLooping(crowdId, true);
        });

        EventManager.subscribe(PeriodStopEvent.class, periodStopEvent -> {
            end.play(volume / 100f);
        });

        EventManager.subscribe(WhistleEvent.class, whistleEvent -> {
            whistle.play(volume / 100f);
        });
    }

    public static void setIntroVolume() {
        intro.setVolume(introId, volume / 100f);
    }

    public static void setCrowdVolume() {
        crowd.setVolume(crowdId, volume / 100f);
    }

    public static void stopMatchSounds() {
        chant.stop();
        crowd.stop();
        end.stop();
        homeGoal.stop();
        intro.stop();
    }

    static void load() {
        bounce = newSound("bounce.ogg");
        celebration = newSound("celebration.ogg");
        chant = newSound("chant.ogg");
        crowd = newSound("crowd.ogg");
        deflect = newSound("deflect.ogg");
        end = newSound("end.ogg");
        hold = newSound("hold.ogg");
        homeGoal = newSound("home_goal.ogg");
        intro = newSound("intro.ogg");
        kick = newSound("kick.ogg");
        net = newSound("net.ogg");
        post = newSound("post.ogg");
        whistle = newSound("whistle.ogg");
    }

    private static Sound newSound(String filename) {
        return Gdx.audio.newSound(Gdx.files.internal("sounds").child(filename));
    }
}
