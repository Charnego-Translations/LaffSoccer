package com.ygames.ysoccer.framework;

public class NetworkInputDevice extends InputDevice {

    public NetworkInputDevice(int port) {
        super(Type.NETWORK, port);
    }

    @Override
    protected void read() {
    }
}
