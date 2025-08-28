package com.ygames.ysoccer.server;

import com.esotericsoftware.kryonet.Server;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.network.dto.events.MatchIntroEventDto;

public class NetworkManager {

    public static void subscribe(Server server) {
        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> {
            server.sendToAllTCP(new MatchIntroEventDto());
        });
    }
}
