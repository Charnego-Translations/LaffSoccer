package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.ygames.ysoccer.events.BallBounceEvent;
import com.ygames.ysoccer.events.BallCollisionEvent;
import com.ygames.ysoccer.events.BallKickEvent;
import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.events.CrowdChantsEvent;
import com.ygames.ysoccer.events.HomeGoalEvent;
import com.ygames.ysoccer.events.KeeperDeflectEvent;
import com.ygames.ysoccer.events.KeeperHoldEvent;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.events.PeriodStopEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Goal;
import com.ygames.ysoccer.network.Network;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.dto.events.BallBounceEventDto;
import com.ygames.ysoccer.network.dto.events.BallCollisionEventDto;
import com.ygames.ysoccer.network.dto.events.BallKickEventDto;
import com.ygames.ysoccer.network.dto.events.CelebrationEventDto;
import com.ygames.ysoccer.network.dto.events.CrowdChantsEventDto;
import com.ygames.ysoccer.network.dto.events.HomeGoalEventDto;
import com.ygames.ysoccer.network.dto.events.KeeperDeflectEventDto;
import com.ygames.ysoccer.network.dto.events.KeeperHoldEventDto;
import com.ygames.ysoccer.network.dto.events.MatchIntroEventDto;
import com.ygames.ysoccer.network.dto.events.PeriodStopEventDto;
import com.ygames.ysoccer.network.dto.events.WhistleEventDto;
import com.ygames.ysoccer.network.mappers.GoalMapper;
import com.ygames.ysoccer.network.mappers.InputDeviceMapper;
import com.ygames.ysoccer.network.mappers.MatchMapper;

import java.io.IOException;

import static com.esotericsoftware.minlog.Log.LEVEL_INFO;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static java.lang.Integer.parseInt;

public class OnlineMatchConnect extends GLScreen {

    private final Client client;
    private final Font font10yellow;
    private final Label errorLabel;

    private final OnlineMatch onlineMatchScreen;

    public OnlineMatchConnect(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_network.jpg");

        Log.set(LEVEL_INFO);
        client = new Client();
        Network.register(client);

        onlineMatchScreen = new OnlineMatch(game, client);

        client.start();
        client.addListener(new Listener() {

            public void received(Connection connection, Object object) {
                if (object instanceof MatchSetupDto) {
                    Gdx.app.postRunnable(() -> {
                        MatchSetupDto matchSetupDto = (MatchSetupDto) object;
                        onlineMatchScreen.setup(matchSetupDto);
                        game.setScreen(onlineMatchScreen);
                    });
                }

                if (object instanceof MatchUpdateDto) {
                    Gdx.app.postRunnable(() -> {
                        MatchUpdateDto matchUpdateDto = (MatchUpdateDto) object;
                        MatchMapper.updateFromDto(onlineMatchScreen.match, matchUpdateDto);
                    });
                    connection.sendUDP(InputDeviceMapper.toDto(onlineMatchScreen.inputDevice));
                }

                // events
                if (object instanceof BallBounceEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new BallBounceEvent(((BallBounceEventDto) object).speed)));

                if (object instanceof BallCollisionEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new BallCollisionEvent(((BallCollisionEventDto) object).strength)));

                if (object instanceof BallKickEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new BallKickEvent(((BallKickEventDto) object).strength)));

                if (object instanceof CelebrationEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new CelebrationEvent()));

                if (object instanceof CrowdChantsEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new CrowdChantsEvent()));

                if (object instanceof HomeGoalEventDto)
                    Gdx.app.postRunnable(() -> {
                        Goal goal = GoalMapper.fromDto(onlineMatchScreen.match, ((HomeGoalEventDto) object).goalDto);
                        onlineMatchScreen.match.goals.add(goal);
                        EventManager.publish(new HomeGoalEvent(goal));
                    });

                if (object instanceof KeeperDeflectEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new KeeperDeflectEvent()));

                if (object instanceof KeeperHoldEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new KeeperHoldEvent()));

                if (object instanceof MatchIntroEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new MatchIntroEvent()));

                if (object instanceof PeriodStopEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new PeriodStopEvent()));

                if (object instanceof WhistleEventDto)
                    Gdx.app.postRunnable(() -> EventManager.publish(new WhistleEvent()));

            }
        });

        font10yellow = new Font(10, 13, 17, 12, 16, new RgbPair(0xFCFCFC, 0xFCFC00));
        font10yellow.load();

        Widget w;

        w = new TitleBar("ONLINE MATCH", 0x1A5581);
        widgets.add(w);

        w = new ServerLabel();
        widgets.add(w);

        w = new ServerButton();
        widgets.add(w);

        w = new TcpPortLabel();
        widgets.add(w);

        w = new TcpPortButton();
        widgets.add(w);

        w = new UdpPortLabel();
        widgets.add(w);

        w = new UdpPortButton();
        widgets.add(w);

        errorLabel = new ErrorLabel();
        widgets.add(errorLabel);

        w = new ConnectButton();
        widgets.add(w);
        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class ServerLabel extends Button {

        ServerLabel() {
            setColor(0x7A7A7A);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 150, 440, 40);
            setText("SERVER", CENTER, font14);
            setActive(false);
        }
    }

    private class ServerButton extends InputButton {

        ServerButton() {
            setColor(0x762B8E);
            setGeometry(game.gui.WIDTH / 2 + 10, 150, 440, 40);
            setText(Settings.serverAddress, CENTER, font14);
            setEntryLimit(28);
        }

        @Override
        public void onChanged() {
            Settings.serverAddress = text;
        }
    }

    private class TcpPortLabel extends Button {

        TcpPortLabel() {
            setColor(0x7A7A7A);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 200, 440, 40);
            setText("TCP PORT", CENTER, font14);
            setActive(false);
        }
    }

    private class TcpPortButton extends InputButton {

        TcpPortButton() {
            setColor(0x762B8E);
            setGeometry(game.gui.WIDTH / 2 + 10, 200, 440, 40);
            setText(Settings.tcpPort, CENTER, font14);
            setEntryLimit(5);
            setInputFilter("[0-9]");
        }

        @Override
        public void onChanged() {
            Settings.tcpPort = parseInt(text);
        }
    }

    private class UdpPortLabel extends Button {

        UdpPortLabel() {
            setColor(0x7A7A7A);
            setGeometry(game.gui.WIDTH / 2 - 10 - 440, 250, 440, 40);
            setText("UDP PORT", CENTER, font14);
            setActive(false);
        }
    }

    private class UdpPortButton extends InputButton {

        UdpPortButton() {
            setColor(0x762B8E);
            setGeometry(game.gui.WIDTH / 2 + 10, 250, 440, 40);
            setText(Settings.udpPort, CENTER, font14);
            setEntryLimit(5);
            setInputFilter("[0-9]");
        }

        @Override
        public void onChanged() {
            Settings.udpPort = parseInt(text);
        }
    }

    private class ErrorLabel extends Label {
        ErrorLabel() {
            setGeometry(game.gui.WIDTH / 2 - 360, 550, 720, 26);
            setText("", CENTER, font10yellow);
        }
    }

    private class ConnectButton extends Button {

        ConnectButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.gui.WIDTH - 180) / 2, 605, 180, 36);
            setText("CONNECT", CENTER, font14);
        }

        @Override
        public void onFire1Down() {
            game.settings.save();
            try {
                errorLabel.setText("");
                client.connect(5000, Settings.serverAddress, Settings.tcpPort, Settings.udpPort);
            } catch (IOException e) {
                errorLabel.setText(e.getMessage().toUpperCase());
            }
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            setText(gettext("EXIT"));
        }

        @Override
        public void onFire1Down() {
            client.stop();
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }
}
