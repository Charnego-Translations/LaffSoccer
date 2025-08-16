package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimeEnter extends MatchState {

    private int enteringCounter;

    MatchStateHalfTimeEnter(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.setStartingPositions();
    }

    @Override
    void onResume() {
        super.onResume();

        scene.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
                if ((enteringCounter % 4) == 0 && enteringCounter / 4 < Const.TEAM_SIZE) {
                    for (int t = HOME; t <= AWAY; t++) {
                        int i = enteringCounter / 4;
                        Player player = scene.team[t].lineup.get(i);
                        player.setState(STATE_REACH_TARGET);
                    }
                }
                enteringCounter += 1;
            }

            scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (enteringCounter / 4 == Const.TEAM_SIZE) {
            return newAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
        }

        return checkCommonConditions();
    }
}
