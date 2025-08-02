package com.ygames.ysoccer.server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class ServerGame extends Game {
    @Override
    public void create() {
        Gdx.app.log(this.getClass().getName(), "started");
    }
}
