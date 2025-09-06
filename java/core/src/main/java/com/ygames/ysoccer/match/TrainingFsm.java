package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDeviceList;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.State.FREE;

public class TrainingFsm extends SceneFsm<Training, TrainingState> {

    enum State {
        FREE,
        REPLAY
    }


    TrainingFsm(Training training, InputDeviceList inputDevices) {
        super(training, inputDevices);

        new TrainingStateFree(this);
        new TrainingStateReplay(this);
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, FREE);
        pushAction(FADE_IN);
    }

    void pushAction(ActionType type, State state) {
        pushAction(type, state.ordinal());
    }
}
