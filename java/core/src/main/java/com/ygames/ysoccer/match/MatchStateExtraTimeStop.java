package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.StateId.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateExtraTimeStop extends MatchState {

    MatchStateExtraTimeStop(MatchFsm fsm) {
        super(EXTRA_TIME_STOP, fsm);
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

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.stateTimer > 3 * SECOND) {
            scene.ball.setPosition(0, 0, 0);
            scene.ball.updatePrediction();

            // redo coin toss
            scene.coinToss = Assets.RANDOM.nextInt(2); // 0 = home begins, 1 = away begins
            scene.kickOffTeam = scene.coinToss;

            // reassign teams sides
            scene.team[HOME].setSide(1 - 2 * Assets.RANDOM.nextInt(2)); // -1 = up, 1 = down
            scene.team[AWAY].setSide(-scene.team[HOME].side);

            scene.period = Match.Period.FIRST_EXTRA_TIME;
            scene.clock = scene.length;

            return newAction(NEW_FOREGROUND, STARTING_POSITIONS);
        }

        return checkCommonConditions();
    }
}
