package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.SceneCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.SceneCamera.Speed.NORMAL;

public class TrainingCamera extends SceneCamera<Training> {

    public TrainingCamera(Training training) {
        super(training, training.getBall());
    }

    @Override
    void updateSettings() {
        switch (scene.state.getId()) {
            case FREE:
                mode = FOLLOW_BALL;
                speed = NORMAL;
                xLimited = true;
                yLimited = true;
                break;
        }
    }
}
