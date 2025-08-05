package com.ygames.ysoccer.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
    }
}
