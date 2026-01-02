package com.ygames.ysoccer.events;

import com.badlogic.gdx.Gdx;

public abstract class GameEvent {
    public GameEvent() {
        Gdx.app.debug("Event fired: ", this.getClass().getSimpleName());
    }
}
