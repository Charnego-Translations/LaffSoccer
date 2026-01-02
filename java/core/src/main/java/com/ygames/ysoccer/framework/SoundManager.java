package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
import com.ygames.ysoccer.events.PlayerGetsBallEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.match.Goal;
import com.ygames.ysoccer.match.Match;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ygames.ysoccer.framework.Assets.EXTENSIONS;
import static com.ygames.ysoccer.framework.FileUtils.randomOrNull;

public class SoundManager {

    public static Map<SoundClass, Set<Sound>> sounds = new HashMap<>();

    public enum SoundClass {
        CHANTS, PAIN
    }

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

    public static Sound shotgun;
    public static Sound button;
    // Specials
    public static Sound noGoalsHalfTime;
    public static Sound manyGoalsHalfTime;
    public static Sound awayTeamTrashing;
    public static Sound localTeamTrashing;
    public static Sound violentMatch;

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
            if (ballKickEvent.isSuperShoot) {
                shotgun.play(volume / 100f);
            } else {
                kick.play(ballKickEvent.strength * volume / 100f);
            }
        });

        EventManager.subscribe(CelebrationEvent.class, celebrationEvent -> {
            celebration.play(volume / 100f);
        });

        EventManager.subscribe(CrowdChantsEvent.class, crowdChantsEvent -> {
            if (crowdChantsEnabled) chant.play(volume / 100f);
        });

        EventManager.subscribe(HomeGoalEvent.class, homeGoalEvent -> {
            homeGoal.play(volume / 100f);
            if (homeGoalEvent.goal.type == Goal.Type.OWN_GOAL) {
                Commentary.INSTANCE.enqueueComment(
                    new Commentary.Comment(Commentary.Comment.Priority.GOAL, CommonComment.pull(CommonComment.CommonCommentType.OWN_GOAL)
                    ));
            } else {
                Commentary.INSTANCE.enqueueComment(
                    new Commentary.Comment(Commentary.Comment.Priority.GOAL, CommonComment.pull(CommonComment.CommonCommentType.GOAL)
                    ));
            }
        });

        EventManager.subscribe(KeeperDeflectEvent.class, keeperDeflectEvent -> {
            deflect.play(0.5f * volume / 100f);
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.KEEPER_SAVE, Commentary.Comment.Priority.HIGH));
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

            if (periodStopEvent.match.period == Match.Period.FIRST_HALF) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.HALF_MATCH, Commentary.Comment.Priority.HIGH));
                Commentary.INSTANCE.enqueueComment(Commentary.halfTime(periodStopEvent.match));
            } else if (periodStopEvent.match.period == Match.Period.SECOND_HALF) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.MATCH_END, Commentary.Comment.Priority.HIGH));
            } else if (periodStopEvent.match.period == Match.Period.FIRST_EXTRA_TIME) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.MATCH_END_EXTRA_TIME, Commentary.Comment.Priority.HIGH));
            } else if (periodStopEvent.match.period == Match.Period.SECOND_EXTRA_TIME) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.MATCH_END, Commentary.Comment.Priority.HIGH));
            } else if (periodStopEvent.match.period == Match.Period.PENALTIES) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonComment.CommonCommentType.PENALTY, Commentary.Comment.Priority.HIGH));
            }

        });

        EventManager.subscribe(WhistleEvent.class, whistleEvent -> {
            whistle.play(volume / 100f);
        });

        EventManager.subscribe(PlayerGetsBallEvent.class, playerGetsBallEvent -> {
            if (playerGetsBallEvent.getTeam().path != null) {
                Sound playerSound = Assets.TeamCommentary.teams.get(FileUtils.getTeamFromFile(playerGetsBallEvent.getTeam().path)).players.get(playerGetsBallEvent.getPlayer().shirtName);
                if (playerSound != null) {
                    Commentary.INSTANCE.enqueueComment(new Commentary.Comment(Commentary.Comment.Priority.LOW, playerSound));
                }
            }
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

        shotgun = newSound("shotgun.ogg"); // Source: https://freesound.org/people/Marregheriti/sounds/266105/
        button = newSound("button.ogg"); // Source: https://freesound.org/people/Snapper4298/sounds/178186/
        sounds.put(SoundClass.CHANTS, loadSoundFolder("chants"));
        sounds.put(SoundClass.PAIN, loadSoundFolder("pain"));

        String specialsFolder = "commentary/special/";

        noGoalsHalfTime = newSound(specialsFolder + "singolesmediotiempo.ogg");
        manyGoalsHalfTime = newSound(specialsFolder + "muchosgolesmediotiempo.ogg");
        awayTeamTrashing = newSound(specialsFolder + "equipovisitantepalizamediotiempo.ogg");
        localTeamTrashing = newSound(specialsFolder + "equipolocalpalizamediotiempo.ogg");

        violentMatch = newSound(specialsFolder + "muchasfaltas.ogg");
    }

    private static Sound newSound(String filename) {
        return Gdx.audio.newSound(Gdx.files.internal("sounds").child(filename));
    }

    public static void stopSounds() {
        crowd.stop();
        end.stop();
        homeGoal.stop();
        intro.stop();
        sounds.forEach((k, v) -> v.forEach(Sound::stop));
    }

    private static Set<Sound> loadSoundFolder(String folder) {
        Set<Sound> result = new HashSet<>();

        FileHandle soundFolder = Gdx.files.local("sounds/" + folder);
        for (FileHandle fileHandle : soundFolder.list()) {
            if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                result.add(newSound(soundFolder.name() + "/" + fileHandle.name()));
            }
        }
        return result;
    }

    public static class CommonComment {

        public enum CommonCommentType {
            CORNER_KICK, FOUL, NOT_FOUL, GOAL, KEEPER_SAVE, OWN_GOAL, PENALTY, PLAYER_SUBSTITUTION, PLAYER_SWAP, THROW_IN, GOAL_KICK, CHITCHAT, KICK_OFF, MATCH_END, HALF_MATCH, MATCH_END_EXTRA_TIME, EXTRA_TIME_FIRST_END, EXTRA_TIME_END
        }

        public static final Map<CommonCommentType, Set<Sound>> commonCommentary = new HashMap<>();
        public static final Map<CommonCommentType, Set<Sound>> commonCommentarySecondary = new HashMap<>();

        static {
            for (CommonCommentType value : CommonCommentType.values()) {
                commonCommentary.put(value, new HashSet<>());
                commonCommentarySecondary.put(value, new HashSet<>());
            }
        }

        public static final Set<Sound> allComments = new HashSet<>();
        public static final Sound[] numbers = new Sound[999];

        public static Sound pull(CommonCommentType type) {
            return randomOrNull(commonCommentary.get(type));
        }

        public static Sound pullSecond(CommonCommentType type) {
            return randomOrNull(commonCommentarySecondary.get(type));
        }

        static void load() {
            FileHandle numbersFolder = Gdx.files.local("sounds/commentary/numbers");
            for (FileHandle fileHandle : numbersFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    String name = fileHandle.nameWithoutExtension();
                    numbers[Integer.parseInt(name)] = Gdx.audio.newSound(fileHandle);
                }
            }
            // Legacy load
            FileHandle commentaryFolder = Gdx.files.local("sounds/commentary");
            for (FileHandle fileHandle : commentaryFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    String name = fileHandle.nameWithoutExtension();
                    for (CommonCommentType type : CommonCommentType.values()) {
                        String fileType = type.name().toLowerCase();
                        if (name.startsWith(fileType)) {
                            commonCommentary.get(type).add(Gdx.audio.newSound(fileHandle));
                        }
                    }
                }
            }
            // Comments in their folders
            for (CommonCommentType commentType : CommonCommentType.values()) {
                commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/");
                for (FileHandle fileHandle : commentaryFolder.list()) {
                    if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                        commonCommentary.get(commentType).add(Gdx.audio.newSound(fileHandle));
                    }
                }
                // Secondary comments
                commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/secondary/");
                for (FileHandle fileHandle : commentaryFolder.list()) {
                    if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                        commonCommentarySecondary.get(commentType).add(Gdx.audio.newSound(fileHandle));
                    }
                }
            }
            allComments.addAll(Arrays.asList(numbers));
            commonCommentary.forEach((k, v) -> allComments.addAll(v));
            commonCommentarySecondary.forEach((k, v) -> allComments.addAll(v));
            allComments.remove(null);
        }

        public static void stop() {
            allComments.forEach(Sound::stop);
        }
    }
}
