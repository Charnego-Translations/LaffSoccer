package com.ygames.ysoccer.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.network.dto.MatchDto;
import com.ygames.ysoccer.network.dto.MatchSettingsDto;
import com.ygames.ysoccer.network.dto.MatchSetupDto;

public class Network {
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(SceneSettings.Time.class);
        kryo.register(Pitch.Type.class);
        kryo.register(MatchSettingsDto.class);
        kryo.register(MatchSetupDto.class);
        kryo.register(MatchDto.class);
    }
}
