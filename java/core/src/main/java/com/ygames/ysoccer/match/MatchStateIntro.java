package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.INTRO;
import static com.ygames.ysoccer.match.MatchFsm.StateId.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateIntro extends MatchState {

    private final int enterDelay = SECOND / 16;

    MatchStateIntro(MatchFsm fsm) {
        super(INTRO, fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayWindVane = true;
        scene.displayRosters = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.clock = 0;
        fsm.matchCompleted = false;
        scene.setIntroPositions();
        scene.resetData();

        EventManager.publish(new MatchIntroEvent());
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        scene.enterPlayers(scene.stateTimer - 1, enterDelay);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            scene.updatePlayers(false);
            scene.playersPhoto();

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.enterPlayersFinished(scene.stateTimer, enterDelay)) {
            if ((scene.team[HOME].fire1Down() != null)
                || (scene.team[AWAY].fire1Down() != null)
                || (scene.stateTimer >= 6 * SECOND)) {
                return newAction(NEW_FOREGROUND, STARTING_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
