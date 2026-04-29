package com.ygames.ysoccer.server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import static com.ygames.ysoccer.framework.GLGame.SERVER_UPDATES_PER_SECOND;

public class ServerLauncher {
    public static void main(String[] args) {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = SERVER_UPDATES_PER_SECOND;
        new HeadlessApplication(new ServerGame(), configuration);
    }
}
