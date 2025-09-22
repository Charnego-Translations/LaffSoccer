package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.MatchFsm.StateId.HALF_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.StateId.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfExtraTimeStop extends MatchState {

    MatchStateHalfExtraTimeStop(MatchFsm fsm) {
        super(HALF_EXTRA_TIME_STOP, fsm);
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

        EventManager.publish(new PeriodStopEvent());

        scene.resetAutomaticInputDevices();
        scene.setPlayersState(STATE_IDLE, null);
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
            scene.ball.setPosition(0, 0, 0);
            scene.ball.updatePrediction();

            scene.swapTeamSides();

            scene.kickOffTeam = 1 - scene.coinToss;

            scene.period = Match.Period.SECOND_EXTRA_TIME;
            scene.clock = scene.length * 105f / 90f;

            return newAction(NEW_FOREGROUND, STARTING_POSITIONS);
        }

        return checkCommonConditions();
    }
}
