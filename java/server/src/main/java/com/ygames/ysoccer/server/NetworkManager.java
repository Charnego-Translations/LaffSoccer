package com.ygames.ysoccer.server;

import com.esotericsoftware.kryonet.Server;
import com.ygames.ysoccer.events.BallBounceEvent;
import com.ygames.ysoccer.events.BallCollisionEvent;
import com.ygames.ysoccer.events.BallKickEvent;
import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.events.MatchIntroEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.network.dto.events.BallBounceEventDto;
import com.ygames.ysoccer.network.dto.events.BallCollisionEventDto;
import com.ygames.ysoccer.network.dto.events.BallKickEventDto;
import com.ygames.ysoccer.network.dto.events.CelebrationEventDto;
import com.ygames.ysoccer.network.dto.events.MatchIntroEventDto;
import com.ygames.ysoccer.network.dto.events.WhistleEventDto;

public class NetworkManager {

    public static void subscribe(Server server) {
        EventManager.subscribe(BallBounceEvent.class, ballBounceEvent -> server.sendToAllTCP(new BallBounceEventDto(ballBounceEvent.speed)));
        EventManager.subscribe(BallCollisionEvent.class, ballCollisionEvent -> server.sendToAllTCP(new BallCollisionEventDto(ballCollisionEvent.strength)));
        EventManager.subscribe(BallKickEvent.class, ballKickEvent -> server.sendToAllTCP(new BallKickEventDto(ballKickEvent.strength)));
        EventManager.subscribe(CelebrationEvent.class, celebrationEvent -> server.sendToAllTCP(new CelebrationEventDto()));
        EventManager.subscribe(MatchIntroEvent.class, matchIntroEvent -> server.sendToAllTCP(new MatchIntroEventDto()));
        EventManager.subscribe(WhistleEvent.class, whistleEvent -> server.sendToAllTCP(new WhistleEventDto()));
    }
}
