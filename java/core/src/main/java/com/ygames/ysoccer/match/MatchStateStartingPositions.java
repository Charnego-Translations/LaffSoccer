package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.MatchFsm.StateId.KICK_OFF;
import static com.ygames.ysoccer.match.MatchFsm.StateId.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateStartingPositions extends MatchState {

    private boolean move;

    MatchStateStartingPositions(MatchFsm fsm) {
        super(STARTING_POSITIONS, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.setStartingPositions();
        scene.setPlayersState(STATE_REACH_TARGET, null);
        scene.setPointOfInterest(scene.ball.x, scene.ball.y);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            return newAction(NEW_FOREGROUND, KICK_OFF);
        }

        return checkCommonConditions();
    }
}
