package com.ygames.ysoccer.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.NetworkInputDevice;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.network.dto.InputDeviceDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.mappers.InputDeviceMapper;
import com.ygames.ysoccer.network.mappers.MatchMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class ServerScreen extends ScreenAdapter {

    private final Server server;
    private final Map<Connection, InputDevice> connections = new ConcurrentHashMap<>();
    private final Match match;
    private boolean matchStarted;
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

                Gdx.app.postRunnable(() -> {
                    addConnection(connection, match);
                });
            }

            public void received(Connection connection, Object object) {
                if (object instanceof InputDeviceDto) {
                    NetworkInputDevice inputDevice = (NetworkInputDevice) connections.get(connection);
                    if (inputDevice != null) {
                        inputDevice.update();
                        InputDeviceMapper.updateFromDto(inputDevice, (InputDeviceDto) object);
                    }
                }
            }

            public void disconnected(Connection connection) {
                Gdx.app.postRunnable(() -> {
                    connections.remove(connection);
                });
            }
        });

        try {
            server.bind(Settings.tcpPort, Settings.udpPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }

    private void addConnection(Connection connection, Match match) {
        switch (connections.size()) {
            case 0:
                connections.put(connection, match.team[HOME].inputDevice);
                break;
            case 1:
                if (connections.containsValue(match.team[HOME].inputDevice)) {
                    connections.put(connection, match.team[AWAY].inputDevice);
                } else {
                    connections.put(connection, match.team[HOME].inputDevice);
                }
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void render(float deltaTime) {
        if (!matchStarted && connections.size() == 2) {
            match.start();
            matchStarted = true;
            Gdx.app.debug("Server", "Match started");
        }

        if (matchStarted && !matchEnded) {
            match.update(deltaTime);
            match.updateCurrentData();
            MatchUpdateDto matchUpdateDto = MatchMapper.toUpdateDto(match);
            server.sendToAllUDP(matchUpdateDto);
        }
    }

    private void quit(boolean matchCompleted) {
        matchEnded = true;
        Gdx.app.debug("Server", "Match ended");
    }
}
