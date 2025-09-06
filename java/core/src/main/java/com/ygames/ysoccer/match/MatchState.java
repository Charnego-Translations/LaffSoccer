package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F1;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.State.BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.State.HELP;
import static com.ygames.ysoccer.match.MatchFsm.State.PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.State.REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;

abstract class MatchState extends SceneState<MatchFsm, Match> {

    boolean checkReplayKey = true;
    boolean checkPauseKey = true;
    boolean checkHelpKey = true;
    boolean checkBenchCall = true;

    final Ball ball;
    InputDevice inputDevice;
    int replayPosition;

    MatchState(MatchFsm.State state, MatchFsm matchFsm) {
        super(state.ordinal(), matchFsm);
        fsm.addState(this);
        this.ball = scene.ball;
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type, MatchFsm.State state) {
        return fsm.newAction(type, state.ordinal());
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, MatchFsm.State state) {
        return fsm.newFadedAction(type, state.ordinal());
    }

    SceneFsm.Action[] checkCommonConditions() {

        if (checkReplayKey && Gdx.input.isKeyPressed(R)) {
            return newFadedAction(HOLD_FOREGROUND, REPLAY);
        }

        if (checkPauseKey && Gdx.input.isKeyPressed(P)) {
            return newAction(HOLD_FOREGROUND, PAUSE);
        }

        if (checkHelpKey && Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, HELP);
        }

        if (checkBenchCall) {
            for (int t = HOME; t <= AWAY; t++) {
                InputDevice inputDevice = scene.team[t].fire2Down();
                if (inputDevice != null) {
                    fsm.benchStatus.team = scene.team[t];
                    fsm.benchStatus.inputDevice = inputDevice;
                    return newAction(HOLD_FOREGROUND, BENCH_ENTER);
                }
            }
        }

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitMatch();
            return null;
        }

        return null;
    }

    void quitMatch() {
        scene.quit();
    }
}
