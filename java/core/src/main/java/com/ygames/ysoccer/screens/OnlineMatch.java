package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;

public class OnlineMatch extends GLScreen {
    public OnlineMatch(GLGame game) {
        super(game);
        usesMouse = false;
        game.disableMouse();
    }
}
