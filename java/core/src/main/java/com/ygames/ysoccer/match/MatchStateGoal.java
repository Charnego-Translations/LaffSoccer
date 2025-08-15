package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.MatchFsm.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoal extends MatchState {

    private Goal goal;
    private boolean replayDone;
    private boolean recordingDone;
    private boolean followBall;

    MatchStateGoal(MatchFsm fsm) {
        super(fsm);

        displayGoalScorer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        checkReplayKey = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
    }

    @Override
    void entryActions() {
        super.entryActions();

        replayDone = false;
        recordingDone = false;
        followBall = true;

        Assets.Sounds.homeGoal.play(Assets.Sounds.volume / 100f);

        goal = scene.goals.get(scene.goals.size() - 1);

        if (scene.settings.commentary) {
            if (goal.type == Goal.Type.OWN_GOAL) {
                int size = Assets.Commentary.ownGoal.size();
                if (size > 0) {
                    Assets.Commentary.ownGoal.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                }
            } else {
                int size = Assets.Commentary.goal.size();
                if (size > 0) {
                    Assets.Commentary.goal.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
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

        scene.actionCamera.setLimited(true, true);
    }

    @Override
    void onResume() {
        super.onResume();

        setCameraMode();
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

            if (followBall) {
                if (scene.ball.v == 0 && scene.ball.vz == 0) {
                    followBall = false;
                    setCameraMode();
                }
            } else {
                scene.actionCamera.setTarget(goal.player.x, goal.player.y);
            }
            scene.actionCamera.update();
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    private void setCameraMode() {
        if (followBall) {
            scene.actionCamera
                    .setMode(FOLLOW_BALL)
                    .setSpeed(NORMAL);
        } else {
            scene.actionCamera
                    .setMode(REACH_TARGET)
                    .setSpeed(FAST);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((scene.ball.v == 0) && (scene.ball.vz == 0)
                && (timer > 3 * SECOND)) {

            if (!recordingDone) {
                scene.recorder.saveHighlight();
                recordingDone = true;
            }

            if (scene.getSettings().autoReplays && !replayDone) {
                replayDone = true;
                return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
            } else {
                scene.ball.setPosition(0, 0, 0);
                scene.ball.updatePrediction();
                scene.actionCamera.setOffset(0, 0);

                return newAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replayDone = true;
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        return checkCommonConditions();
    }
}
