package com.ygames.ysoccer.screens;

import com.esotericsoftware.kryonet.Client;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;

public class OnlineMatch extends GLScreen {

    public OnlineMatch(GLGame game, Client client) {
        super(game);
        usesMouse = false;
        game.disableMouse();
    }
}
