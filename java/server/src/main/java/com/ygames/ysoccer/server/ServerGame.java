package com.ygames.ysoccer.server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.ygames.ysoccer.competitions.Friendly;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.InputDeviceList;
import com.ygames.ysoccer.framework.NetworkInputDevice;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.network.Network;

import static com.esotericsoftware.minlog.Log.LEVEL_INFO;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.ControlMode.PLAYER;

public class ServerGame extends Game {
    @Override
    public void create() {
        Settings settings = new Settings();
        Log.set(LEVEL_INFO);
        Server server = new Server();
        Network.register(server);
        NetworkManager.subscribe(server);

        Assets.loadCore(settings);

        Friendly friendly = new Friendly();
        MatchSettings matchSettings = new MatchSettings(friendly, settings);
        matchSettings.setup();

        Match match = friendly.getMatch();
        FileHandle homeFileHandle = Assets.teamsRootFolder.child(Settings.serverHomeTeam);
        FileHandle awayFileHandle = Assets.teamsRootFolder.child(Settings.serverAwayTeam);
        Team homeTeam = Assets.json.fromJson(Team.class, homeFileHandle.readString("UTF-8"));
        Team awayTeam = Assets.json.fromJson(Team.class, awayFileHandle.readString("UTF-8"));

        homeTeam.path = Settings.serverHomeTeam;
        awayTeam.path = Settings.serverAwayTeam;

        homeTeam.inputDevice = new NetworkInputDevice(0);
        homeTeam.controlMode = PLAYER;
        awayTeam.inputDevice = new NetworkInputDevice(1);
        awayTeam.controlMode = PLAYER;
        match.setTeam(HOME, homeTeam);
        match.setTeam(AWAY, awayTeam);
        match.init(new InputDeviceList(), matchSettings, friendly);

        setScreen(new ServerScreen(server, match));
    }
}
