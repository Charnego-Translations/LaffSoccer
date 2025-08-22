package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

abstract class SceneState<SceneFsmT extends SceneFsm<SceneT, ?>, SceneT extends Scene<SceneFsmT, ?>> {

    private int id;
    final SceneFsmT fsm;
    final SceneT scene;
    int timer;

    SceneState(SceneFsmT fsm) {
        this.fsm = fsm;
        this.scene = fsm.getScene();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    SceneFsm.Action[] newAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newAction(type, stateId);
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type) {
        return newAction(type, -1);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type) {
        return newFadedAction(type, -1);
    }

    void onResume() {
        setDisplayFlags();
    }

    void onPause() {
    }

    abstract SceneFsm.Action[] checkConditions();

    boolean checkId(int id) {
        return (this.id == id);
    }

    void setDisplayFlags() {
    }
}
