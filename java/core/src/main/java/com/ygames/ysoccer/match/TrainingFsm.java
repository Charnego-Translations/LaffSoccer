package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.InputDeviceList;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.StateId.FREE;

public class TrainingFsm extends SceneFsm<Training, TrainingState> {

    enum StateId implements SceneState.Id {
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
}
