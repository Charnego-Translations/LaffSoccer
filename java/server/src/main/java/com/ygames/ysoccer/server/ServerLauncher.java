package com.ygames.ysoccer.server;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import static com.ygames.ysoccer.framework.GLGame.SERVER_UPDATES_PER_SECOND;

/** Launches the headless application. Can be converted into a utilities project or a server application. */
public class ServerLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Application createApplication() {
        // Note: you can use a custom ApplicationListener implementation for the headless project instead of YSoccer.
        return new HeadlessApplication(new ServerGame(), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = SERVER_UPDATES_PER_SECOND; // When this value is negative, YSoccer#render() is never called.
        //// If the above line doesn't compile, it is probably because the project libGDX version is older.
        //// In that case, uncomment and use the below line.
        //configuration.renderInterval = -1f; // When this value is negative, YSoccer#render() is never called.
        return configuration;
    }
}
