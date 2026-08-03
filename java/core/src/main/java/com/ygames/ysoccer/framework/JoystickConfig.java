package com.ygames.ysoccer.framework;

public class JoystickConfig extends InputDeviceConfig {

    public String name;
    public int xAxis = -1;
    public int left = -1;
    public int right = -1;
    public int yAxis = -1;
    public int up = -1;
    public int down = -1;
    public int button1 = -1;
    public int button2 = -1;

    public JoystickConfig() {
        super(InputDevice.Type.JOYSTICK);
    }

    public JoystickConfig(String name) {
        super(InputDevice.Type.JOYSTICK);
        this.name = name;
    }

    public boolean isConfigured() {
        return isXAxisConfigured() && isYAxisConfigured() && button1 != -1 && button2 != -1;
    }

    public boolean isXAxisConfigured() {
        return xAxis != -1 || (left != -1 && right != -1);
    }

    public boolean isYAxisConfigured() {
        return yAxis != -1 || (up != -1 && down != -1);
    }

    public void reset() {
        xAxis = -1;
        left = -1;
        right = -1;
        yAxis = -1;
        up = -1;
        down = -1;
        button1 = -1;
        button2 = -1;
    }

    public boolean isAnalogXAxis() {
        return xAxis != -1;
    }

    public boolean isAnalogYAxis() {
        return yAxis != -1;
    }

    public void setXAxis(int axisIndex) {
        xAxis = axisIndex;
        left = -1;
        right = -1;
    }

    public void setYAxis(int axisIndex) {
        yAxis = axisIndex;
        up = -1;
        down = -1;
    }

    public void setLeft(int buttonIndex) {
        left = buttonIndex;
        xAxis = -1;
    }

    public void setRight(int buttonIndex) {
        right = buttonIndex;
        xAxis = -1;
    }

    public void setUp(int buttonIndex) {
        up = buttonIndex;
        yAxis = -1;
    }

    public void setDown(int buttonIndex) {
        down = buttonIndex;
        yAxis = -1;
    }
}
