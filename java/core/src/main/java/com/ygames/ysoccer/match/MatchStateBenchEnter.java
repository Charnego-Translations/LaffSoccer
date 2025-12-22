package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_EXIT;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchEnter extends MatchState {

    MatchStateBenchEnter(MatchFsm fsm) {
        super(BENCH_ENTER, fsm);

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

        fsm.benchStatus.oldTarget.set(scene.camera.getCurrentTarget());

        fsm.benchStatus.selectedPosition = -1;
        fsm.benchStatus.substPosition = -1;

        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = scene.team[t].lineup.get(i);
                if (scene.team[t].usesAutomaticInputDevice()) {
                    player.setInputDevice(player.ai);
                }
                player.setState(STATE_REACH_TARGET);
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

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (scene.camera.getTargetDistance() < 1) {
            Coach coach = fsm.benchStatus.team.coach;
            coach.status = Coach.Status.STAND;
            return newAction(NEW_FOREGROUND, BENCH_SUBSTITUTIONS);
        }

        if (fsm.benchStatus.inputDevice.xReleased()) {
            return newAction(NEW_FOREGROUND, BENCH_EXIT);
        }

        return checkCommonConditions();
    }
}
