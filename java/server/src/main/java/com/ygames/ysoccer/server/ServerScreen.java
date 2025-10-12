package com.ygames.ysoccer.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygames.ysoccer.framework.NetworkInputDevice;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.network.dto.InputDeviceDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.mappers.InputDeviceMapper;
import com.ygames.ysoccer.network.mappers.MatchMapper;

import java.io.IOException;

public class ServerScreen extends ScreenAdapter {

    private final Server server;
    private final Match match;
    private boolean matchStarted;
    private boolean connected;
    private boolean matchEnded;

    public ServerScreen(Server server, Match match) {
        this.server = server;
        this.match = match;

        match.listener = new Match.MatchListener() {
            public void quitMatch(boolean matchCompleted) {
                quit(matchCompleted);
            }
        };

        server.addListener(new Listener() {
            public void connected(Connection connection) {
                MatchSetupDto matchSetupDto = MatchSetupDto.toDto(match);
                server.sendToTCP(connection.getID(), matchSetupDto);
                connected = true;
            }

            public void received(Connection connection, Object object) {
                if (object instanceof InputDeviceDto) {
                    NetworkInputDevice inputDevice = (NetworkInputDevice) match.team[0].inputDevice;
                    inputDevice.update();

                    InputDeviceMapper.updateFromDto(inputDevice, (InputDeviceDto) object);
                }
            }
        });

        try {
            server.bind(Settings.tcpPort, Settings.udpPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }

    @Override
    public void render(float deltaTime) {
        if (!matchStarted && connected) {
            match.start();
            matchStarted = true;
            Gdx.app.log("Server", "Match started");
        }

        if (matchStarted && !matchEnded) {
//            match.team[HOME].inputDevice.update();
            match.update(deltaTime);
            match.updateCurrentData();
            MatchUpdateDto matchUpdateDto = MatchMapper.toUpdateDto(match);
            server.sendToAllUDP(matchUpdateDto);
        }
    }

    private void quit(boolean matchCompleted) {
        matchEnded = true;
        Gdx.app.log("Server", "Match ended");
    }
}
