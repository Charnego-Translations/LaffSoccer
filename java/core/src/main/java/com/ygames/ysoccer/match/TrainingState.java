package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDevice;

abstract class TrainingState extends SceneState<TrainingFsm, Training> {

    boolean displayControlledPlayer;
    boolean displayPause;
    boolean displayReplayGui;
    boolean displayReplayControls;

    // convenience references
    final Team[] team;
    final Ball ball;
    InputDevice inputDevice;
    int replayPosition;

    TrainingState(TrainingFsm.StateId state, TrainingFsm trainingFsm) {
        super(state, trainingFsm);
        fsm.addState(this);
        this.team = scene.team;
        this.ball = scene.ball;
    }

    void quitTraining() {
        scene.quit();
    }
}
