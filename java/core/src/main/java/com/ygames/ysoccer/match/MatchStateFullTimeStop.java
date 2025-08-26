package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFullTimeStop extends MatchState {

    MatchStateFullTimeStop(MatchFsm fsm) {
        super(fsm);

        checkBenchCall = false;
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

        scene.clock = scene.length;
        fsm.matchCompleted = true;

        EventManager.publish(new PeriodStopEvent());

        scene.resetAutomaticInputDevices();
        scene.setPlayersState(STATE_IDLE, null);

        Team winner = scene.competition.getMatchWinner();
        if (winner != null) {
            for (int i = 1; i < TEAM_SIZE; i++) {
                if (Assets.random.nextFloat() < 0.7f) {
                    Player player = winner.lineup.get(i);
                    player.setState(STATE_CELEBRATION);
                }
            }
        }
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

            scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (timer > 3 * SECOND) {
            if (scene.competition.getFinalWinner() != null) {
                return newAction(NEW_FOREGROUND, STATE_FINAL_CELEBRATION);
            } else {
                return newAction(NEW_FOREGROUND, STATE_END_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
