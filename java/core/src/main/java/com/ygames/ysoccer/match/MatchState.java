package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F1;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;

abstract class MatchState extends SceneState<MatchFsm, Match> {

    boolean displayGoalScorer;
    boolean displayRosters;
    boolean displayScore;
    boolean displayPenaltiesScore;
    boolean displayStatistics;
    boolean displayBenchPlayers;
    boolean displayBenchFormation;
    boolean displayTacticsSwitch;
    boolean displayHelp;
    boolean displayPause;
    boolean displayReplayGui;
    boolean displayReplayControls;
    boolean displayHighlightsGui;

    boolean checkReplayKey = true;
    boolean checkPauseKey = true;
    boolean checkHelpKey = true;
    boolean checkBenchCall = true;

    final Ball ball;
    InputDevice inputDevice;
    int replayPosition;

    MatchState(MatchFsm matchFsm) {
        super(matchFsm);

        this.ball = scene.ball;
    }

    SceneFsm.Action[] checkCommonConditions() {

        if (checkReplayKey && Gdx.input.isKeyPressed(R)) {
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        if (checkPauseKey && Gdx.input.isKeyPressed(P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (checkHelpKey && Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        if (checkBenchCall) {
            for (int t = HOME; t <= AWAY; t++) {
                InputDevice inputDevice = scene.team[t].fire2Down();
                if (inputDevice != null) {
                    fsm.benchStatus.team = scene.team[t];
                    fsm.benchStatus.inputDevice = inputDevice;
                    return newAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
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
