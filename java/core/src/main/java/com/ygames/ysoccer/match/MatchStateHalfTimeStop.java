package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HALF_TIME_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimeStop extends MatchState {

    MatchStateHalfTimeStop(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.end.play(Assets.Sounds.volume / 100f);

        scene.resetAutomaticInputDevices();
        scene.setPlayersState(STATE_IDLE, null);
    }

    @Override
    void onResume() {
        super.onResume();

        scene.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            scene.updateBall();
            scene.ball.inFieldKeep();

            scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (timer > 3 * SECOND) {
            return newAction(NEW_FOREGROUND, STATE_HALF_TIME_POSITIONS);
        }

        return checkCommonConditions();
    }
}
