package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.MatchFsm.State.PENALTIES_END;
import static com.ygames.ysoccer.match.MatchFsm.State.PENALTIES_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltiesKick extends MatchState {

    private boolean isKicking;

    MatchStatePenaltiesKick(MatchFsm fsm) {
        super(PENALTIES_KICK, fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayWindVane = true;
        scene.displayPenaltiesScore = true;
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        scene.penalty.kicker.setTarget(0, scene.penalty.side * (Const.PENALTY_SPOT_Y - 7));
        scene.penalty.kicker.setState(STATE_REACH_TARGET);

        scene.actionCamera.setMode(STILL);
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
            scene.penalty.kicker.setState(STATE_IDLE);
            return newAction(NEW_FOREGROUND, PENALTIES_END);
        }

        return checkCommonConditions();
    }
}
