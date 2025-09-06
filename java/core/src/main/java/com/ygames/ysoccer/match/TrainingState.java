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

    TrainingState(TrainingFsm.State state, TrainingFsm trainingFsm) {
        super(state.ordinal(), trainingFsm);
        fsm.addState(this);
        this.team = scene.team;
        this.ball = scene.ball;
    }

    void quitTraining() {
        scene.quit();
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, TrainingFsm.State stateId) {
        return fsm.newFadedAction(type, stateId.ordinal());
    }
}
