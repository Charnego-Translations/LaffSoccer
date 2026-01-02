package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.ThrowInEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.MatchFsm.StateId.THROW_IN;
import static com.ygames.ysoccer.match.MatchFsm.StateId.THROW_IN_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateThrowInStop extends MatchState {

    private boolean move;

    MatchStateThrowInStop(MatchFsm fsm) {
        super(THROW_IN_STOP, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        EventManager.publish(new WhistleEvent());
        EventManager.publish(new ThrowInEvent());

        fsm.throwInPosition.set(scene.ball.xSide * Const.TOUCH_LINE, scene.ball.y);

        scene.resetAutomaticInputDevices();
        scene.setPlayersState(STATE_REACH_TARGET, null);
    }

    @Override
    void onResume() {
        super.onResume();

        scene.setPointOfInterest(fsm.throwInPosition);
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

            move = scene.updatePlayers(true);
            scene.updateTeamTactics();

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            scene.ball.setPosition(fsm.throwInPosition);
            scene.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, THROW_IN);
        }

        return checkCommonConditions();
    }
}
