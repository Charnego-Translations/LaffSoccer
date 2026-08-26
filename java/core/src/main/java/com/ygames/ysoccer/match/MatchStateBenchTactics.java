package com.ygames.ysoccer.match;


import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_TACTICS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchTactics extends MatchState {

    MatchStateBenchTactics(MatchFsm fsm) {
        super(BENCH_TACTICS, fsm);

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
        scene.displayTacticsSwitch = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.benchSelectedTactics = scene.benchTeam.tactics;
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

        // change selected tactics
        if (fsm.benchInputDevice.yMoved()) {
            scene.benchSelectedTactics = EMath.rotate(scene.benchSelectedTactics, 0, 17, fsm.benchInputDevice.y1);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        // set selected tactics and go back to bench
        if (fsm.benchInputDevice.fire1Down()
            || fsm.benchInputDevice.xReleased()) {
            if (scene.benchSelectedTactics != scene.benchTeam.tactics) {
                Coach coach = scene.benchTeam.coach;
                coach.status = Coach.Status.CALL;
                coach.timer = 500;
                scene.benchTeam.tactics = scene.benchSelectedTactics;
            }
            return newAction(NEW_FOREGROUND, BENCH_SUBSTITUTIONS);
        }

        return checkCommonConditions();
    }
}
