package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;

public abstract class Scene<SceneFsmT extends SceneFsm<?, SceneStateT>, SceneStateT extends SceneState<SceneFsmT, ?>> {

    SceneStateT.Id stateId;
    SceneFsmT fsm;
    public SceneStateT state;
    protected int subframe;
    protected SceneSettings settings;

    Vector2 pointOfInterest;

    public SceneCamera<?> camera;
    public int light;

    final int[] vCameraX = new int[Const.REPLAY_SUBFRAMES];
    final int[] vCameraY = new int[Const.REPLAY_SUBFRAMES];
    int cameraX;
    int cameraY;

    public SceneFsmT getFsm() {
        return fsm;
    }

    public SceneStateT getState() {
        return state;
    }

    void setState(SceneStateT state) {
        this.state = state;
        this.setStateId(state == null ? null : state.getId());
    }

    abstract SceneStateT.Id getStateId();

    public void setStateId(SceneStateT.Id stateId) {
        this.stateId = stateId;
    }

    public void start() {
        fsm.start();
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    void setPointOfInterest(float x, float y) {
        pointOfInterest.set(x, y);
    }

    void setPointOfInterest(Vector2 v) {
        pointOfInterest.set(v);
    }

    public void setCamera(SceneCamera camera) {
        this.camera = camera;
    }

    abstract Player getNearestOfAll();

    public void setBallOwner(Player player) {
        setBallOwner(player, true);
    }

    abstract public void setBallOwner(Player player, boolean updateGoalOwner);

    abstract void quit();

    abstract void save();

    protected abstract void updateCurrentData();

    public abstract void clearDisplayFlags();
}
