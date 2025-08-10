package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDevice;

abstract class TrainingState extends SceneState {

    boolean displayControlledPlayer;
    boolean displayPause;
    boolean displayReplayGui;
    boolean displayReplayControls;

    // convenience references
    final Training training;
    final Team[] team;
    final Ball ball;
    InputDevice inputDevice;
    int replayPosition;

    TrainingState(TrainingFsm trainingFsm) {
        super(trainingFsm);

        this.training = trainingFsm.getTraining();
        this.team = training.team;
        this.ball = training.ball;
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    void quitTraining() {
        training.quit();
    }
}
