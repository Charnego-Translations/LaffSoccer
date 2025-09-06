package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.State.INTRO;
import static com.ygames.ysoccer.match.MatchFsm.State.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateIntro extends MatchState {

    private final int enterDelay = SECOND / 16;
    private boolean stillCamera;

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

        stillCamera = true;
        scene.clock = 0;
        fsm.matchCompleted = false;
        scene.setIntroPositions();
        scene.resetData();

        EventManager.publish(new MatchIntroEvent());
    }

    @Override
    void onResume() {
        super.onResume();

        setCameraMode();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        scene.enterPlayers(timer - 1, enterDelay);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            scene.updatePlayers(false);
            scene.playersPhoto();

            scene.nextSubframe();

            scene.save();

            if (stillCamera && timer > SECOND) {
                stillCamera = false;
                setCameraMode();
            }
            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    private void setCameraMode() {
        scene.actionCamera.setMode(stillCamera ? STILL : FOLLOW_BALL);
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.enterPlayersFinished(timer, enterDelay)) {
            if ((scene.team[HOME].fire1Down() != null)
                || (scene.team[AWAY].fire1Down() != null)
                || (timer >= 6 * SECOND)) {
                return newAction(NEW_FOREGROUND, STARTING_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
