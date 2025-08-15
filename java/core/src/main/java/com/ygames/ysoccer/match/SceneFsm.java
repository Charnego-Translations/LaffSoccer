package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.InputDeviceList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

abstract class SceneFsm<SceneT extends Scene<?, SceneStateT>, SceneStateT extends SceneState<?, SceneT>> {

    enum ActionType {
        NONE,
        NEW_FOREGROUND,
        FADE_IN,
        FADE_OUT,
        HOLD_FOREGROUND,
        RESTORE_FOREGROUND
    }

    static class Action {
        final ActionType type;
        final int stateId;
        int timer;

        Action(ActionType type, int stateId) {
            this.type = type;
            this.stateId = stateId;
        }

        Action(ActionType type) {
            this(type, -1);
        }

        void update() {
            if (timer > 0) {
                timer -= 1;
            }
        }
    }

    private final SceneT scene;

    private final List<SceneStateT> states;
    private SceneStateT holdState;

    private final ArrayDeque<Action> actions;
    private Action currentAction;
    final InputDeviceList inputDevices;

    SceneFsm(SceneT scene, InputDeviceList inputDevices) {
        this.scene = scene;
        this.inputDevices = inputDevices;
        states = new ArrayList<>();
        actions = new ArrayDeque<>();
    }

    SceneT getScene() {
        return scene;
    }

    int addState(SceneStateT state) {
        states.add(state);
        state.setId(states.size());
        return state.getId();
    }

    public abstract void start();

    SceneStateT getHoldState() {
        return holdState;
    }

    void think(float deltaTime) {
        boolean doUpdate = true;

        // fade in/out
        if (currentAction != null && currentAction.type == FADE_OUT) {
            scene.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == FADE_IN) {
            scene.light = 255 - 8 * (currentAction.timer - 1);
            doUpdate = false;
        }

        // update current state
        if (scene.state != null && doUpdate) {
            if (currentAction != null
                && (currentAction.type == NEW_FOREGROUND || currentAction.type == RESTORE_FOREGROUND)) {
                scene.state.onResume();
            }
            if (currentAction != null
                && (currentAction.type == HOLD_FOREGROUND)) {
                holdState.onPause();
            }
            scene.state.doActions(deltaTime);

            Action[] newActions = scene.state.checkConditions();
            if (newActions != null) {
                for (Action action : newActions) {
                    actions.offer(action);
                }
            }
        }

        // update current action
        if (currentAction != null) {
            currentAction.update();
            if (currentAction.timer == 0) {
                currentAction = null;
            }
        }

        // get new action
        if (currentAction == null) {
            if (actions.size() > 0) {
                pollAction();
            }
        }
    }

    private void pollAction() {

        currentAction = actions.pop();

        switch (currentAction.type) {

            case NONE:
                break;

            case FADE_IN:
                currentAction.timer = 32;
                break;

            case FADE_OUT:
                currentAction.timer = 32;
                break;

            case NEW_FOREGROUND:
                if (scene.state != null) {
                    scene.state.exitActions();
                }
                scene.state = searchState(currentAction.stateId);
                Gdx.app.debug("NEW_FOREGROUND", scene.state == null ? "null" : scene.state.getClass().getSimpleName());
                break;

            case HOLD_FOREGROUND:
                holdState = scene.state;
                scene.state = searchState(currentAction.stateId);
                Gdx.app.debug("HOLD_FOREGROUND", scene.state == null ? "null" : scene.state.getClass().getSimpleName());
                break;

            case RESTORE_FOREGROUND:
                scene.state.exitActions();
                scene.state = holdState;
                holdState = null;
                break;

        }
    }

    private SceneStateT searchState(int id) {
        for (int i = 0; i < states.size(); i++) {
            SceneStateT s = states.get(i);
            if (s.checkId(id)) {
                s.entryActions();
                return s;
            }
        }
        return null;
    }

    void pushAction(ActionType type) {
        pushAction(type, -1);
    }

    void pushAction(ActionType type, int stateId) {
        actions.offer(new Action(type, stateId));
    }

    Action[] newAction(ActionType type, int stateId) {
        return new Action[]{new Action(type, stateId)};
    }

    Action[] newFadedAction(ActionType type, int stateId) {
        return new Action[]{
            new Action(FADE_OUT),
            new Action(type, stateId),
            new Action(FADE_IN)
        };
    }
}
