package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.State.HALF_TIME_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.State.HALF_TIME_WAIT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimePositions extends MatchState {

    private boolean move;

    MatchStateHalfTimePositions(MatchFsm fsm) {
        super(HALF_TIME_POSITIONS, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayWindVane = true;
        scene.displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.ball.setPosition(0, 0, 0);
        scene.ball.updatePrediction();

        scene.actionCamera
            .setMode(REACH_TARGET)
            .setTarget(0, 0)
            .setOffset(0, 0)
            .setSpeed(FAST);

        scene.period = Match.Period.UNDEFINED;
        scene.clock = scene.length * 45f / 90f;

        scene.setPlayersTarget(Const.TOUCH_LINE + 80, 0);
        scene.setPlayersState(STATE_OUTSIDE, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            move = scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            return newAction(NEW_FOREGROUND, HALF_TIME_WAIT);
        }

        return checkCommonConditions();
    }
}
