package com.ygames.ysoccer.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.BallDto;
import com.ygames.ysoccer.network.dto.CoachDto;
import com.ygames.ysoccer.network.dto.CompetitionDto;
import com.ygames.ysoccer.network.dto.FrameDataDto;
import com.ygames.ysoccer.network.dto.KitDto;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.dto.PlayerDto;
import com.ygames.ysoccer.network.dto.TeamDto;
import com.ygames.ysoccer.network.dto.events.BallBounceEventDto;
import com.ygames.ysoccer.network.dto.events.BallCollisionEventDto;
import com.ygames.ysoccer.network.dto.events.MatchIntroEventDto;
import com.ygames.ysoccer.network.dto.events.WhistleEventDto;

import java.util.ArrayList;

public class Network {

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(ArrayList.class);
        kryo.register(SceneSettings.Time.class);
        kryo.register(Pitch.Type.class);
        kryo.register(MatchSettingsDto.class);
        kryo.register(FrameDataDto.class);
        kryo.register(BallDto.class);
        kryo.register(PlayerDto.class);
        kryo.register(KitDto.class);
        kryo.register(CoachDto.class);
        kryo.register(TeamDto.class);
        kryo.register(TeamDto[].class);
        kryo.register(MatchSetupDto.class);
        kryo.register(MatchDto.class);
        kryo.register(MatchUpdateDto.class);
        kryo.register(CompetitionDto.class);

        // events
        kryo.register(BallBounceEventDto.class);
        kryo.register(BallCollisionEventDto.class);
        kryo.register(MatchIntroEventDto.class);
        kryo.register(WhistleEventDto.class);
    }
}
