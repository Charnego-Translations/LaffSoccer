package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.StateId.MAIN;
import static com.ygames.ysoccer.match.MatchFsm.StateId.PENALTY_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltyKick extends MatchState {

    private boolean isKicking;

    MatchStatePenaltyKick(MatchFsm fsm) {
        super(PENALTY_KICK, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayWindVane = true;
        scene.displayScore = true;
    }

    @Override
    void entryActions() {
        super.entryActions();
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        scene.penalty.kicker.setTarget(0, scene.penalty.side * (Const.PENALTY_SPOT_Y - 7));
        scene.penalty.kicker.setState(STATE_REACH_TARGET);

        scene.actionCamera
            .setMode(FOLLOW_BALL)
            .setSpeed(FAST)
            .setLimited(true, true);
    }

    @Override
    void onPause() {
        super.onPause();

        scene.penalty.kicker.setTarget(-40 * scene.ball.ySide, scene.penalty.side * (Const.PENALTY_SPOT_Y - 45));
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

        if (!move && !isKicking) {
            EventManager.publish(new WhistleEvent());

            scene.penalty.kicker.setState(STATE_PENALTY_KICK_ANGLE);
            if (scene.penalty.kicker.team.usesAutomaticInputDevice()) {
                scene.penalty.kicker.inputDevice = scene.penalty.kicker.team.inputDevice;
            }

            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.ball.v > 0) {
            scene.setPlayersState(STATE_STAND_RUN, scene.penalty.kicker);
            scene.penaltyScorer = scene.penalty.kicker;
            scene.penalty = null;
            return newAction(NEW_FOREGROUND, MAIN);
        }

        return checkCommonConditions();
    }
}
