package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_EXIT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;
import static java.lang.Math.min;

class MatchStateBenchExit extends MatchState {

    MatchStateBenchExit(MatchFsm fsm) {
        super(BENCH_EXIT, fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Coach coach = scene.benchTeam.coach;
        coach.status = Coach.Status.BENCH;

        // reset positions
        int substitutes = min(scene.getSettings().benchSize, scene.benchTeam.lineup.size() - TEAM_SIZE);
        for (int i = 0; i < substitutes; i++) {
            Player player = scene.benchTeam.lineup.get(TEAM_SIZE + i);
            if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                player.setState(STATE_BENCH_SITTING);
            }
        }
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            scene.updateBall();
            scene.ball.inFieldKeep();

            scene.updatePlayers(true);

            scene.updateCoaches();

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (scene.stateTimer > 1.5f * SECOND) {
            return newAction(RESTORE_FOREGROUND);
        }

        return checkCommonConditions();
    }
}
