package com.ygames.ysoccer.events;

import com.badlogic.gdx.Gdx;
import lombok.ToString;

@ToString
public abstract class GameEvent {
    public GameEvent() {
        Gdx.app.debug(this.getClass().getSimpleName(), this.toString());
    }
}
