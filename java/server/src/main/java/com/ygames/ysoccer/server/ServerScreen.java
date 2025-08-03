package com.ygames.ysoccer.server;

import com.badlogic.gdx.ScreenAdapter;
import com.esotericsoftware.kryonet.Server;

public class ServerScreen extends ScreenAdapter {

    private final Server server;

    public ServerScreen(Server server) {
        this.server = server;
    }

    @Override
    public void render(float delta) {
    }
}
