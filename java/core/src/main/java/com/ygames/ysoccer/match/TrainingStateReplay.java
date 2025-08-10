package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.Settings;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

class TrainingStateReplay extends TrainingState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    private boolean keySlow;
    private boolean keyPause;

    TrainingStateReplay(TrainingFsm fsm) {
        super(fsm);

        displayReplayGui = true;

        displayControlledPlayer = Settings.showPlayerNumber;
    }

    @Override
    void entryActions() {
        super.entryActions();

        subframe0 = training.subframe;

        paused = false;
        slowMotion = false;

        // control keys
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // position of current frame in the replay vector
        replayPosition = 0;

        inputDevice = null;
        displayReplayControls = false;
    }

    @Override
    void exitActions() {
        training.subframe = subframe0;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // toggle pause
        if (Gdx.input.isKeyPressed(Input.Keys.P) && !keyPause) {
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
            for (InputDevice d : training.game.inputDevices) {
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
            replayPosition = EMath.slide(replayPosition, 1, Const.REPLAY_SUBFRAMES, speed);

            training.subframe = (subframe0 + replayPosition) % Const.REPLAY_SUBFRAMES;
        }

        displayPause = paused;
        displayReplayControls = inputDevice != null;
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        // quit on ESC
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            return quitAction();
        }

        // quit on fire button
        for (InputDevice d : training.game.inputDevices) {
            if (d.fire1Down()) {
                return quitAction();
            }
        }

        // quit on last position
        if ((replayPosition == Const.REPLAY_SUBFRAMES) && (inputDevice == null)) {
            return quitAction();
        }

        return null;
    }

    private SceneFsm.Action[] quitAction() {
        // if final frame is different from starting frame then fade out
        if (replayPosition != Const.REPLAY_SUBFRAMES) {
            return newFadedAction(RESTORE_FOREGROUND);
        } else {
            return newAction(RESTORE_FOREGROUND);
        }
    }
}
