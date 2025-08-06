package com.ygames.ysoccer.server;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.network.Network;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;

import java.io.IOException;

import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;

public class ServerGame extends Game {
    @Override
    public void create() {
        Settings settings = new Settings();
        Log.set(LEVEL_TRACE);
        Server server = new Server();
        Network.register(server);

        Assets.loadStrings(settings);
        MatchSettings matchSettings = new MatchSettings(new Friendly(), settings);
        matchSettings.setup();

        server.addListener(new Listener() {
            public void connected(Connection connection) {
                MatchSetupDto matchSetupDto = MatchSetupDto.toDto(matchSettings);
                server.sendToTCP(connection.getID(), matchSetupDto);
            }
        });

        try {
            server.bind(Settings.tcpPort, Settings.udpPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();

        setScreen(new ServerScreen(server));
    }
}
