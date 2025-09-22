package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

abstract class SceneState<SceneFsmT extends SceneFsm<SceneT, ?>, SceneT extends Scene<SceneFsmT, ?>> {

    public interface Id {
    }

    Id id;
    final SceneFsmT fsm;
    final SceneT scene;
    int timer;

    SceneState(Id id, SceneFsmT fsm) {
        this.id = id;
        this.fsm = fsm;
        this.scene = fsm.getScene();
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    void entryActions() {
        timer = 0;
    }

    void exitActions() {
    }

    void doActions(float deltaTime) {
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {
            timer += 1;

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type, Id stateId) {
        return fsm.newAction(type, stateId);
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type) {
        return newAction(type, null);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, Id stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type) {
        return newFadedAction(type, null);
    }

    void onResume() {
        setDisplayFlags();
    }

    void onPause() {
    }

    abstract SceneFsm.Action[] checkConditions();

    boolean checkId(Id id) {
        return (this.id == id);
    }

    void setDisplayFlags() {
    }
}
