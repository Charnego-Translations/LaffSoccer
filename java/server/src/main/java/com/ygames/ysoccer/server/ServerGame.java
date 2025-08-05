package com.ygames.ysoccer.server;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.network.Network;

import java.io.IOException;

import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;

public class ServerGame extends Game {
    @Override
    public void create() {
        Settings settings = new Settings();
        Log.set(LEVEL_TRACE);
        Server server = new Server();
        Network.register(server);

        try {
            server.bind(Settings.tcpPort, Settings.udpPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();

        setScreen(new ServerScreen(server));
    }
}
