package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.MatchFsm.StateId.PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.StateId.PENALTIES_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltiesStop extends MatchState {

    MatchStatePenaltiesStop(MatchFsm fsm) {
        super(PENALTIES_STOP, fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
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

            scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.stateTimer > 3 * SECOND) {
            scene.ball.setPosition(0, -Const.PENALTY_SPOT_Y, 0);
            scene.ball.updatePrediction();

            scene.penaltyKickingTeam = Assets.random.nextInt(2);

            scene.period = Match.Period.PENALTIES;

            scene.addPenalties(5);

            return newAction(NEW_FOREGROUND, PENALTIES);
        }

        return checkCommonConditions();
    }
}
