package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDevice;

abstract class TrainingState extends SceneState<TrainingFsm, Training> {

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

        this.training = trainingFsm.getScene();
        this.team = training.team;
        this.ball = training.ball;
    }

    void quitTraining() {
        training.quit();
    }
}
