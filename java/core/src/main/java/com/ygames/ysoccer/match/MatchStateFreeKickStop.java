package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.SoundManager;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.FREE_KICK;
import static com.ygames.ysoccer.match.MatchFsm.StateId.FREE_KICK_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFreeKickStop extends MatchState {

    private boolean allPlayersReachingTarget;
    private final ArrayList<Player> playersReachingTarget;

    MatchStateFreeKickStop(MatchFsm fsm) {
        super(FREE_KICK_STOP, fsm);

        playersReachingTarget = new ArrayList<>();
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        EventManager.publish(new WhistleEvent());

        if (scene.settings.commentary) {
            int size = Assets.Commentary.foul.size();
            if (size > 0) {
                Assets.Commentary.foul.get(Assets.random.nextInt(size)).play(SoundManager.volume / 100f);
            }
        }

        // set the player targets relative to foul zone
        // even before moving the ball itself
        ball.updateZone(scene.foul.position.x, scene.foul.position.y);
        scene.updateTeamTactics();
        scene.foul.player.team.keepTargetDistanceFrom(scene.foul.position);
        if (scene.foul.isDirectShot()) {
            scene.foul.player.team.setFreeKickBarrier();
        }
        scene.team[HOME].lineup.get(0).setTarget(0, scene.team[HOME].side * (Const.GOAL_LINE - 8));
        scene.team[AWAY].lineup.get(0).setTarget(0, scene.team[AWAY].side * (Const.GOAL_LINE - 8));

        scene.resetAutomaticInputDevices();

        allPlayersReachingTarget = false;
        playersReachingTarget.clear();
    }

    @Override
    void onResume() {
        super.onResume();

        scene.setPointOfInterest(scene.foul.position);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();

                allPlayersReachingTarget = true;
                for (int t = HOME; t <= AWAY; t++) {
                    for (int i = 0; i < TEAM_SIZE; i++) {
                        Player player = scene.team[t].lineup.get(i);

                        // wait for tackle and down states to finish
                        if (player.checkState(STATE_TACKLE) || player.checkState(STATE_DOWN)) {
                            allPlayersReachingTarget = false;
                        } else if (!playersReachingTarget.contains(player)) {
                            player.setState(STATE_REACH_TARGET);
                            playersReachingTarget.add(player);
                        }
                    }
                }
            }

            scene.updateBall();
            ball.inFieldKeep();
            ball.collisionFlagPosts();
            ball.collisionGoal();
            ball.collisionJumpers();
            ball.collisionNet();
            ball.collisionNetOut();

            scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (allPlayersReachingTarget) {
            ball.setPosition(scene.foul.position.x, scene.foul.position.y, 0);
            ball.updatePrediction();

            return newAction(NEW_FOREGROUND, FREE_KICK);
        }

        return checkCommonConditions();
    }
}
