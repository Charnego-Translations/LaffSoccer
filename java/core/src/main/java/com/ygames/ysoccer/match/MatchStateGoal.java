package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.events.HomeGoalEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.SoundManager;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.GOAL;
import static com.ygames.ysoccer.match.MatchFsm.StateId.REPLAY;
import static com.ygames.ysoccer.match.MatchFsm.StateId.STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoal extends MatchState {

    private Goal goal;
    private boolean replayDone;
    private boolean recordingDone;

    MatchStateGoal(MatchFsm fsm) {
        super(GOAL, fsm);

        checkReplayKey = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
        scene.displayGoalScorer = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        replayDone = false;
        recordingDone = false;

        EventManager.publish(new HomeGoalEvent());

        goal = scene.goals.get(scene.goals.size() - 1);

        if (scene.settings.commentary) {
            if (goal.type == Goal.Type.OWN_GOAL) {
                int size = Assets.Commentary.ownGoal.size();
                if (size > 0) {
                    Assets.Commentary.ownGoal.get(Assets.random.nextInt(size)).play(SoundManager.volume / 100f);
                }
            } else {
                int size = Assets.Commentary.goal.size();
                if (size > 0) {
                    Assets.Commentary.goal.get(Assets.random.nextInt(size)).play(SoundManager.volume / 100f);
                }
            }
        }

        if (scene.team[HOME].side == scene.ball.ySide) {
            scene.kickOffTeam = HOME;
        } else if (scene.team[AWAY].side == scene.ball.ySide) {
            scene.kickOffTeam = AWAY;
        } else {
            throw new RuntimeException("cannot decide kick_off_team!");
        }

        scene.resetAutomaticInputDevices();

        scene.setPointOfInterest(scene.ball.x, scene.ball.y);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // set states
        if (goal.type == Goal.Type.OWN_GOAL) {
            scene.setStatesForOwnGoal(goal);
        } else {
            scene.setStatesForGoal(goal);
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            scene.updateBall();
            scene.ball.collisionGoal();
            scene.ball.collisionNet();

            scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((scene.ball.v == 0) && (scene.ball.vz == 0)
            && (scene.stateTimer > 3 * SECOND)) {

            if (!recordingDone) {
                scene.recorder.saveHighlight();
                recordingDone = true;
            }

            if (scene.getSettings().autoReplays && !replayDone) {
                replayDone = true;
                return newFadedAction(HOLD_FOREGROUND, REPLAY);
            } else {
                scene.ball.setPosition(0, 0, 0);
                scene.ball.updatePrediction();

                return newAction(NEW_FOREGROUND, STARTING_POSITIONS);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replayDone = true;
            return newFadedAction(HOLD_FOREGROUND, REPLAY);
        }

        return checkCommonConditions();
    }
}
