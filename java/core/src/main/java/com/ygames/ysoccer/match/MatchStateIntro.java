package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateIntro extends MatchState {

    private final int enterDelay = SECOND / 16;
    private boolean stillCamera;

    MatchStateIntro(MatchFsm fsm) {
        super(fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
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

        Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
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
                    || (timer >= 5 * SECOND)) {
                return newAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
            }
        }

        return checkCommonConditions();
    }
}
