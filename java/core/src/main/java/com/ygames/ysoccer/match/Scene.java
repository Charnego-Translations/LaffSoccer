package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;

public abstract class Scene {

    SceneFsm fsm;
    protected int subframe;
    protected SceneSettings settings;

    Vector2 pointOfInterest;

    ActionCamera actionCamera;
    public int light;

    final int[] vCameraX = new int[Const.REPLAY_SUBFRAMES];
    final int[] vCameraY = new int[Const.REPLAY_SUBFRAMES];

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

    abstract Player getNearestOfAll();

    public void setBallOwner(Player player) {
        setBallOwner(player, true);
    }

    abstract public void setBallOwner(Player player, boolean updateGoalOwner);

    abstract void quit();

    abstract void save();
}
