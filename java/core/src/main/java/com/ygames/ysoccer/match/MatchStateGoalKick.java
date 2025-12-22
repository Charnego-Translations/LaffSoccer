package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.GOAL_KICK;
import static com.ygames.ysoccer.match.MatchFsm.StateId.MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoalKick extends MatchState {

    private Player goalKickPlayer;
    private boolean isKicking;

    MatchStateGoalKick(MatchFsm fsm) {
        super(GOAL_KICK, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
        scene.displayScore = true;
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        goalKickPlayer = fsm.goalKickTeam.lineupAtPosition(0);
        goalKickPlayer.setTarget(scene.ball.x, scene.ball.y + 6 * scene.ball.ySide);
        goalKickPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();

        goalKickPlayer.setTarget(scene.ball.x / 4, fsm.goalKickTeam.side * (GOAL_LINE - 8));
        scene.team[HOME].updateTactics(true);
        scene.team[AWAY].updateTactics(true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            scene.updateBall();
            scene.ball.inFieldKeep();

            move = scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            EventManager.publish(new WhistleEvent());

            goalKickPlayer.setState(PlayerFsm.Id.STATE_GOAL_KICK);
            if (goalKickPlayer.team.usesAutomaticInputDevice()) {
                goalKickPlayer.inputDevice = goalKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.ball.v > 0) {
            scene.setPlayersState(STATE_STAND_RUN, goalKickPlayer);
            return newAction(NEW_FOREGROUND, MAIN);
        }

        return checkCommonConditions();
    }
}
