package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Coach.Status.LOOK_BENCH;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_EXIT;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_FORMATION;
import static com.ygames.ysoccer.match.MatchFsm.StateId.BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_OUT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static java.lang.Math.min;

class MatchStateBenchSubstitutions extends MatchState {

    MatchFsm.BenchStatus benchStatus;

    MatchStateBenchSubstitutions(MatchFsm fsm) {
        super(BENCH_SUBSTITUTIONS, fsm);

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
        scene.displayBenchPlayers = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        benchStatus = fsm.benchStatus;
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

        // move selection
        if (benchStatus.inputDevice.yMoved()) {
            int substitutes = min(scene.getSettings().benchSize, benchStatus.team.lineup.size() - TEAM_SIZE);

            // if remaining substitutions
            if (benchStatus.team.substitutionsCount < scene.getSettings().substitutions) {
                benchStatus.selectedPosition = EMath.rotate(benchStatus.selectedPosition, -1, substitutes - 1, benchStatus.inputDevice.y1);
            }

            // reset positions
            for (int i = 0; i < substitutes; i++) {
                Player player = benchStatus.team.lineup.get(TEAM_SIZE + i);
                if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                    player.setState(STATE_BENCH_SITTING);
                }
            }

            // move selected player
            if (benchStatus.selectedPosition != -1) {
                Player player = benchStatus.team.lineup.get(TEAM_SIZE + benchStatus.selectedPosition);
                if (!player.getState().checkId(STATE_SUBSTITUTED)) {
                    // coach calls player
                    Coach coach = benchStatus.team.coach;
                    coach.status = LOOK_BENCH;
                    coach.timer = 250;

                    player.setState(STATE_BENCH_STANDING);
                }
            }
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (benchStatus.inputDevice.fire1Down()) {
            if (benchStatus.selectedPosition == -1) {
                return newAction(NEW_FOREGROUND, BENCH_FORMATION);
            } else {
                // if no previous selection
                if (benchStatus.substPosition == -1) {

                    // out the player for substitution
                    Player player = benchStatus.team.lineup.get(TEAM_SIZE + benchStatus.selectedPosition);

                    if (!player.getState().checkId(STATE_SUBSTITUTED)) {

                        player.setState(STATE_BENCH_OUT);

                        benchStatus.substPosition = TEAM_SIZE + benchStatus.selectedPosition;
                        benchStatus.selectedPosition = benchStatus.team.nearestBenchPlayerByRole(player.role);

                        return newAction(NEW_FOREGROUND, BENCH_FORMATION);
                    }
                }
            }
        }

        if (benchStatus.inputDevice.xReleased()) {
            return newAction(NEW_FOREGROUND, BENCH_EXIT);
        }

        return checkCommonConditions();
    }
}
