package com.ygames.ysoccer.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.network.dto.BallDto;
import com.ygames.ysoccer.network.dto.BallUpdateDto;
import com.ygames.ysoccer.network.dto.CoachDto;
import com.ygames.ysoccer.network.dto.CompetitionDto;
import com.ygames.ysoccer.network.dto.FrameDataDto;
import com.ygames.ysoccer.network.dto.KitDto;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.dto.MatchUpdateDto;
import com.ygames.ysoccer.network.dto.PlayerDto;
import com.ygames.ysoccer.network.dto.PlayerUpdateDto;
import com.ygames.ysoccer.network.dto.TeamDto;
import com.ygames.ysoccer.network.dto.TeamUpdateDto;
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
        kryo.register(BallUpdateDto.class);
        kryo.register(Player.Role.class);
        kryo.register(Hair.Color.class);
        kryo.register(Skin.Color.class);
        kryo.register(PlayerDto.class);
        kryo.register(PlayerUpdateDto.class);
        kryo.register(KitDto.class);
        kryo.register(CoachDto.class);
        kryo.register(TeamDto.class);
        kryo.register(TeamDto[].class);
        kryo.register(TeamUpdateDto.class);
        kryo.register(TeamUpdateDto[].class);
        kryo.register(MatchSetupDto.class);
        kryo.register(MatchDto.class);
        kryo.register(MatchUpdateDto.class);
        kryo.register(CompetitionDto.class);

        // events
        kryo.register(BallBounceEventDto.class);
        kryo.register(BallCollisionEventDto.class);
        kryo.register(BallKickEventDto.class);
        kryo.register(CelebrationEventDto.class);
        kryo.register(CrowdChantsEventDto.class);
        kryo.register(HomeGoalEventDto.class);
        kryo.register(KeeperDeflectEventDto.class);
        kryo.register(KeeperHoldEventDto.class);
        kryo.register(MatchIntroEventDto.class);
        kryo.register(PeriodStopEventDto.class);
        kryo.register(WhistleEventDto.class);
    }
}
