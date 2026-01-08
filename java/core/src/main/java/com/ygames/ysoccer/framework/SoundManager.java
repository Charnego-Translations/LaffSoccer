package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.events.BallBounceEvent;
import com.ygames.ysoccer.events.BallCollisionEvent;
import com.ygames.ysoccer.events.BallKickEvent;
import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.events.CornerKickEvent;
import com.ygames.ysoccer.events.CrowdChantsEvent;
import com.ygames.ysoccer.events.GoalKickEvent;
import com.ygames.ysoccer.events.HomeGoalEvent;
import com.ygames.ysoccer.events.KeeperDeflectEvent;
import com.ygames.ysoccer.events.KeeperHoldEvent;
import com.ygames.ysoccer.events.KickOffEvent;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.events.PenaltyEvent;
import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.events.PlayerGetsBallEvent;
import com.ygames.ysoccer.events.SubstitutionEvent;
import com.ygames.ysoccer.events.TackleEvent;
import com.ygames.ysoccer.events.ThrowInEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.commentary.Comment;
import com.ygames.ysoccer.framework.commentary.CommentPriority;
import com.ygames.ysoccer.framework.commentary.Commentary;
import com.ygames.ysoccer.framework.commentary.CommonComment;
import com.ygames.ysoccer.framework.commentary.CommonCommentType;
import com.ygames.ysoccer.framework.commentary.TeamCommentary;
import com.ygames.ysoccer.match.Goal;
import com.ygames.ysoccer.match.Match;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ygames.ysoccer.framework.Assets.EXTENSIONS;
import static com.ygames.ysoccer.framework.EMath.randomPick;

public class SoundManager {

    public static final Map<SoundClass, Set<Sound>> SOUNDS = new HashMap<>();

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
            if (crowdChantsEnabled) {
                crowdChantsEvent.sound.play(volume / 160f);
            }
        });

        EventManager.subscribe(HomeGoalEvent.class, homeGoalEvent -> {
            homeGoal.play(volume / 100f);
            if (homeGoalEvent.goal.type == Goal.Type.OWN_GOAL) {
                Commentary.INSTANCE.enqueueComment(
                    new Comment(CommentPriority.GOAL, CommonComment.pull(CommonCommentType.OWN_GOAL)
                    ));
            } else {
                Commentary.INSTANCE.enqueueComment(
                    new Comment(CommentPriority.GOAL, CommonComment.pull(CommonCommentType.GOAL)
                    ));
            }
        });

        EventManager.subscribe(KeeperDeflectEvent.class, keeperDeflectEvent -> {
            deflect.play(0.5f * volume / 100f);
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.KEEPER_SAVE, CommentPriority.HIGH));
        });

        EventManager.subscribe(KeeperHoldEvent.class, keeperHoldEvent -> {
            hold.play(0.5f * volume / 100f);
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.KEEPER_SAVE, CommentPriority.HIGH));
        });

        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            introId = intro.play(volume / 100f);
            crowdId = crowd.play(volume / 100f);
            crowd.setLooping(crowdId, true);
        });

        EventManager.subscribe(PeriodStopEvent.class, periodStopEvent -> {
            end.play(volume / 100f);

            switch (periodStopEvent.match.period) {
                case FIRST_HALF:
                    Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.HALF_MATCH, CommentPriority.HIGH));
                    Commentary.INSTANCE.enqueueComment(Commentary.halfTime(periodStopEvent.match));
                    break;

                case SECOND_HALF:
                    Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.MATCH_END, CommentPriority.HIGH));
                    Commentary.INSTANCE.enqueueMatchEndComment(periodStopEvent.match);
                    break;

                case FIRST_EXTRA_TIME:
                    Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.EXTRA_TIME_FIRST_END, CommentPriority.HIGH));
                    break;

                case SECOND_EXTRA_TIME:
                    Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.EXTRA_TIME_END, CommentPriority.HIGH));
                    break;

                case PENALTIES:
                    Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.MATCH_END_EXTRA_TIME, CommentPriority.HIGH));
                    Commentary.INSTANCE.enqueueMatchEndComment(periodStopEvent.match);
                    break;

                default:
                    Gdx.app.log("SoundManager PeriodStopEvent", "Unknown period: " + periodStopEvent.match.period);
                    break;
            }
        });

        EventManager.subscribe(KeeperDeflectEvent.class, keeperDeflectEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.KEEPER_DEFLECT, CommentPriority.HIGH));
        });

        EventManager.subscribe(KeeperHoldEvent.class, keeperHoldEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.KEEPER_SAVE, CommentPriority.HIGH));
        });

        EventManager.subscribe(PenaltyEvent.class, penaltyEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.PENALTY, CommentPriority.HIGH));
        });

        EventManager.subscribe(TackleEvent.class, tackleEvent -> {
            if (tackleEvent.opponent == null) return;
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(tackleEvent.isFault ? CommonCommentType.FOUL : CommonCommentType.NOT_FOUL, CommentPriority.HIGH));
            playVariations(SoundClass.PAIN);
        });

        EventManager.subscribe(WhistleEvent.class, whistleEvent -> {
            whistle.play(volume / 100f);
        });

        EventManager.subscribe(ThrowInEvent.class, throwInEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.THROW_IN, CommentPriority.HIGH));
        });

        EventManager.subscribe(KickOffEvent.class, kickOffEvent -> {
            if (kickOffEvent.period == Match.Period.FIRST_HALF) {
                Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.KICK_OFF, CommentPriority.HIGH));
            }
        });

        EventManager.subscribe(PlayerGetsBallEvent.class, playerGetsBallEvent -> {
            if (playerGetsBallEvent.getTeam().path != null) {
                Sound playerSound = TeamCommentary.teams.get(FileUtils.getTeamFromFile(playerGetsBallEvent.getTeam().path)).players.get(playerGetsBallEvent.getPlayer().shirtName);
                if (playerSound != null) {
                    EMath.oneIn(2.5f, () -> Commentary.INSTANCE.enqueueComment(new Comment(CommentPriority.LOW, playerSound)));
                }
            }
        });

        EventManager.subscribe(GoalKickEvent.class, goalKickEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.GOAL_KICK, CommentPriority.HIGH));
        });

        EventManager.subscribe(CornerKickEvent.class, cornerKickEvent -> {
            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.CORNER_KICK, CommentPriority.HIGH));
        });

        EventManager.subscribe(SubstitutionEvent.class, substitutionEvent -> {

            Sound playerIn = TeamCommentary.teams.get(FileUtils.getTeamFromFile(substitutionEvent.team.path)).players.get(substitutionEvent.in.shirtName);
            Sound playerOut = TeamCommentary.teams.get(FileUtils.getTeamFromFile(substitutionEvent.team.path)).players.get(substitutionEvent.out.shirtName);

            Commentary.INSTANCE.enqueueComment(Commentary.getComment(CommonCommentType.PLAYER_SUBSTITUTION, CommentPriority.HIGH));

            //TODO add substitution comment
            if (playerIn != null && playerOut != null) {
                Commentary.INSTANCE.enqueueComment(new Comment(CommentPriority.HIGH, playerIn), new Comment(CommentPriority.HIGH, playerOut));
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
        SOUNDS.put(SoundClass.CHANTS, loadSoundFolder("chants"));
        SOUNDS.put(SoundClass.PAIN, loadSoundFolder("pain"));

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

    public static Sound getSound(SoundClass soundClass) {
        return randomPick(SOUNDS.get(soundClass));
    }

    public static void stopSounds() {
        crowd.stop();
        end.stop();
        homeGoal.stop();
        intro.stop();
        SOUNDS.forEach((k, v) -> v.forEach(Sound::stop));
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

    public static void playVariations(Sound sound, boolean rndPanning) {
        sound.play(Assets.RANDOM.nextFloat() / 1.4f, (Assets.RANDOM.nextFloat() / 2) + 0.5f, rndPanning ? (Assets.RANDOM.nextFloat() * 2) - 1 : 0);
    }
    public static void playVariations(SoundClass soundClass) {
        playVariations(SoundManager.getSound(soundClass), false);
    }


}
