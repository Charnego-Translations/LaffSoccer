package com.ygames.ysoccer.server;

import com.badlogic.gdx.ScreenAdapter;
import com.esotericsoftware.kryonet.Server;
import com.ygames.ysoccer.match.Match;

public class ServerScreen extends ScreenAdapter {

    private final Server server;
    private final Match match;

    public ServerScreen(Server server, Match match) {
        this.server = server;
        this.match = match;
    }

    @Override
    public void render(float deltaTime) {
        match.update(deltaTime);
    }
}
