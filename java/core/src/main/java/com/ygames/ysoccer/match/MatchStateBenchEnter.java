package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.WARP;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_EXIT;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchEnter extends MatchState {

    MatchStateBenchEnter(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        fsm.benchStatus.oldTarget.set(scene.actionCamera.getCurrentTarget());

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

        scene.actionCamera
            .setMode(REACH_TARGET)
            .setTarget(-0.55f * TOUCH_LINE, -20)
            .setLimited(false, true)
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

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if (scene.actionCamera.getTargetDistance() < 1) {
            Coach coach = fsm.benchStatus.team.coach;
            coach.status = Coach.Status.STAND;
            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        if (fsm.benchStatus.inputDevice.xReleased()) {
            return newAction(NEW_FOREGROUND, STATE_BENCH_EXIT);
        }

        return checkCommonConditions();
    }
}
