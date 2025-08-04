package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;

import java.io.IOException;

import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;
import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;
import static java.lang.Integer.parseInt;

public class OnlineMatchConnect extends GLScreen {

    private final Client client;
    private final Font font10yellow;
    private final Label errorLabel;

    public OnlineMatchConnect(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_network.jpg");

        Log.set(LEVEL_TRACE);
        client = new Client();
        client.start();
        client.addListener(new Listener() {

            public void connected(Connection connection) {
                Gdx.app.log("client", "connected to " + connection.getRemoteAddressTCP());
                game.setScreen(new OnlineMatch(game));
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
        }

        @Override
        public void onChanged() {
            Settings.udpPort = parseInt(text);
            Gdx.app.log("udpPort", Settings.udpPort + "");
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
