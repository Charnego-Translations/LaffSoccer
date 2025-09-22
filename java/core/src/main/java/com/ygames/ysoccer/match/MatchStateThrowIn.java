package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.MatchFsm.StateId.MAIN;
import static com.ygames.ysoccer.match.MatchFsm.StateId.THROW_IN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateThrowIn extends MatchState {

    private Player throwInPlayer;
    private boolean isThrowingIn;

    MatchStateThrowIn(MatchFsm fsm) {
        super(THROW_IN, fsm);
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

        isThrowingIn = false;

        fsm.throwInTeam.updateFrameDistance();
        fsm.throwInTeam.findNearest();
        throwInPlayer = fsm.throwInTeam.near1;

        throwInPlayer.setTarget(scene.ball.x, scene.ball.y);
        throwInPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();

        scene.updateTeamTactics();

        scene.ball.setPosition(fsm.throwInPosition);
        scene.ball.updatePrediction();
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

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isThrowingIn) {

            EventManager.publish(new WhistleEvent());

            throwInPlayer.setState(STATE_THROW_IN_ANGLE);
            if (throwInPlayer.team.usesAutomaticInputDevice()) {
                throwInPlayer.inputDevice = throwInPlayer.team.inputDevice;
            }
            isThrowingIn = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (Math.abs(scene.ball.x) < Const.TOUCH_LINE) {
            scene.setPlayersState(STATE_STAND_RUN, throwInPlayer);
            return newAction(NEW_FOREGROUND, MAIN);
        }

        return checkCommonConditions();
    }
}
