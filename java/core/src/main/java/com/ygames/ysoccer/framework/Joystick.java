package com.ygames.ysoccer.framework;

import com.badlogic.gdx.controllers.Controller;

import static java.lang.Math.round;

class Joystick extends InputDevice {

    private Controller controller;
    private JoystickConfig config;

    Joystick(Controller controller, JoystickConfig config, int port) {
        super(Type.JOYSTICK, port);
        this.controller = controller;
        this.config = config;
    }

    @Override
    protected void read() {
        if (config.isAnalogXAxis()) {
            x0 = round(this.controller.getAxis(config.xAxis));
        } else {
            x0 = (this.controller.getButton(config.left) ? -1 : 0) + (this.controller.getButton(config.right) ? 1 : 0);
        }
        if (config.isAnalogYAxis()) {
            y0 = round(this.controller.getAxis(config.yAxis));
        } else {
            y0 = (this.controller.getButton(config.up) ? -1 : 0) + (this.controller.getButton(config.down) ? 1 : 0);
        }
        fire10 = this.controller.getButton(config.button1);
        fire20 = this.controller.getButton(config.button2);
    }
}
