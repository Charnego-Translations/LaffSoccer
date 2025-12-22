package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.MatchFsm.StateId.END;
import static com.ygames.ysoccer.match.MatchFsm.StateId.HIGHLIGHTS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHighlights extends MatchState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    private boolean keySlow;
    private boolean keyPause;

    MatchStateHighlights(MatchFsm fsm) {
        super(HIGHLIGHTS, fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayWindVane = true;
        scene.displayHighlightsGui = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        // store initial frame
        subframe0 = scene.subframe;

        paused = false;
        slowMotion = false;

        // control keys
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // position of current frame in the highlights vector
        replayPosition = 0;

        inputDevice = null;

        scene.recorder.loadHighlight();
    }

    @Override
    void exitActions() {
        scene.displayReplayControls = false;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // toggle pause
        if (inputDevice == null && Gdx.input.isKeyPressed(Input.Keys.P) && !keyPause) {
            paused = !paused;
        }
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // toggle slow-motion
        if (Gdx.input.isKeyPressed(Input.Keys.R) && !keySlow) {
            slowMotion = !slowMotion;
        }
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);

        // set/unset controlling device
        if (inputDevice == null) {
            for (InputDevice d : fsm.inputDevices) {
                if (d.fire2Down()) {
                    inputDevice = d;
                    paused = false;
                }
            }
        } else {
            if (inputDevice.fire2Down()) {
                inputDevice = null;
            }
        }

        // set speed
        int speed;
        if (inputDevice != null) {
            speed = 12 * inputDevice.x1 - 4 * inputDevice.y1 + 8 * Math.abs(inputDevice.x1) * inputDevice.y1;
        } else if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        } else {
            speed = GLGame.SUBFRAMES;
        }

        // set position
        if (!paused) {
            replayPosition = EMath.slide(replayPosition, GLGame.SUBFRAMES / 2, Const.REPLAY_SUBFRAMES, speed);

            scene.subframe = (subframe0 + replayPosition) % Const.REPLAY_SUBFRAMES;
        }

        scene.displayPause = paused;
        scene.displayReplayControls = inputDevice != null;
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        // quit on fire button
        for (InputDevice d : fsm.inputDevices) {
            if (d.fire1Down()) {
                return newFadedAction(NEW_FOREGROUND, END);
            }
        }

        // quit on finish
        if (replayPosition == Const.REPLAY_SUBFRAMES) {
            scene.recorder.nextHighlight();
            if (scene.recorder.hasEnded()) {
                return newFadedAction(NEW_FOREGROUND, END);
            } else {
                return newFadedAction(NEW_FOREGROUND, HIGHLIGHTS);
            }
        }

        return checkCommonConditions();
    }
}
