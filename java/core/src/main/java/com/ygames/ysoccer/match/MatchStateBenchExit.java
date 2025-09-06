package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.WARP;
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

        Coach coach = fsm.benchStatus.team.coach;
        coach.status = Coach.Status.BENCH;

        // reset positions
        int substitutes = min(scene.getSettings().benchSize, fsm.benchStatus.team.lineup.size() - TEAM_SIZE);
        for (int i = 0; i < substitutes; i++) {
            Player player = fsm.benchStatus.team.lineup.get(TEAM_SIZE + i);
            if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                player.setState(STATE_BENCH_SITTING);
            }
        }

        scene.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(fsm.benchStatus.oldTarget)
                .setSpeed(WARP);
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

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (scene.actionCamera.getTargetDistance() < 1) {
            return newAction(RESTORE_FOREGROUND);
        }

        return checkCommonConditions();
    }
}
