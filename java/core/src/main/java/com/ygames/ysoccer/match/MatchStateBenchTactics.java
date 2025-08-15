package com.ygames.ysoccer.match;


import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchTactics extends MatchState {

    MatchFsm.BenchStatus benchStatus;

    MatchStateBenchTactics(MatchFsm fsm) {
        super(fsm);

        displayTacticsSwitch = true;

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
    }

    @Override
    void entryActions() {
        super.entryActions();

        benchStatus = fsm.benchStatus;
        benchStatus.selectedTactics = benchStatus.team.tactics;
        scene.actionCamera.setMode(STILL);
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

        // change selected tactics
        if (benchStatus.inputDevice.yMoved()) {
            benchStatus.selectedTactics = EMath.rotate(benchStatus.selectedTactics, 0, 17, benchStatus.inputDevice.y1);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        // set selected tactics and go back to bench
        if (benchStatus.inputDevice.fire1Down()
                || benchStatus.inputDevice.xReleased()) {
            if (benchStatus.selectedTactics != benchStatus.team.tactics) {
                Coach coach = benchStatus.team.coach;
                coach.status = Coach.Status.CALL;
                coach.timer = 500;
                benchStatus.team.tactics = benchStatus.selectedTactics;
            }
            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        return checkCommonConditions();
    }
}
