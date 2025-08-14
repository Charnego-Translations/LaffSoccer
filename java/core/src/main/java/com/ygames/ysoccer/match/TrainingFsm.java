package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDeviceList;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

public class TrainingFsm extends SceneFsm<Training> {

    private static int STATE_FREE;
    static int STATE_REPLAY;

    TrainingFsm(Training training, InputDeviceList inputDevices) {
        super(training, inputDevices);

        STATE_FREE = addState(new TrainingStateFree(this));
        STATE_REPLAY = addState(new TrainingStateReplay(this));
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, STATE_FREE);
        pushAction(FADE_IN);
    }

    TrainingState getState() {
        return (TrainingState) super.getState();
    }

    public Training getTraining() {
        return (Training) getScene();
    }
}
