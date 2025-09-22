package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.HALF_TIME_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.StateId.HALF_TIME_WAIT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimeWait extends MatchState {

    MatchStateHalfTimeWait(MatchFsm fsm) {
        super(HALF_TIME_WAIT, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayWindVane = true;
        scene.displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.swapTeamSides();

        scene.kickOffTeam = 1 - scene.coinToss;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.team[HOME].fire1Down() != null
            || scene.team[AWAY].fire1Down() != null
            || (timer > 3 * SECOND)) {
            scene.period = Match.Period.SECOND_HALF;
            return newAction(NEW_FOREGROUND, HALF_TIME_ENTER);
        }

        return checkCommonConditions();
    }
}
