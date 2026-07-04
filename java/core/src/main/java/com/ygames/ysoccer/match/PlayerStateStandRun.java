package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class PlayerStateStandRun extends PlayerState {

    // Easy mode assisted ball control: eases the ball toward a point just ahead of the
    // player instead of projecting it along a fixed angle/speed, so a sudden change of
    // direction doesn't send it off on its own trajectory.
    // Must stay under Match.EASY_MODE_BALL_OWNER_MAX_DISTANCE, or the ball will
    // repeatedly lose/regain ownership out there (and re-fire PlayerGetsBallEvent commentary).
    private static final float EASY_MODE_LEAD_DISTANCE = 12f;
    private static final float EASY_MODE_CATCH_UP_RATE = 15f;
    private static final float EASY_MODE_MAX_CATCH_UP_SPEED = 400f;

    PlayerStateStandRun(PlayerFsm fsm) {
        super(STATE_STAND_RUN, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        if (ball.owner != player) {
            player.getPossession();
        }

        // ball control
        if ((ball.owner == player) && (ball.z < Const.PLAYER_H)) {

            if (scene.settings.easyMode) {
                assistBallControl();
            } else {

                // ball-player angle difference
                float signedAngleDiff = EMath.signedAngleDiff(player.ballAngle, player.a);

                if (player.ballDistance < 9) {
                    // Control slice
                    // lowest ball control skill: +/- 70 degrees
                    // highest ball control skill: +/- 91 degrees
                    float slice = 70 + 3 * player.skills.control;

                    // just ahead of feet: push forward
                    if (Math.abs(signedAngleDiff) <= slice && player.ballDistance > 3.5f) {
                        // Control efficacy
                        // lowest ball control skill: 1 + 0.060f * player.v
                        // highest ball control skill: 1 + 0.032f * player.v
                        float m = 1 + (0.06f - 0.004f * player.skills.control);
                        ball.v = Math.max(ball.v, (m * ((player.ballDistance < 9) ? 1 : 0)) * player.v);

                        // angle correction
                        if (Math.abs(signedAngleDiff) > 22.5) {
                            ball.a = player.a - signedAngleDiff / 2f;
                        } else {
                            ball.a = player.a;
                        }
                    }

                    // too back: push fast forward
                    else {
                        if (player.v > 0) {
                            ball.v = Math.max(ball.v, 1.2f * player.v);
                            ball.a = player.a;
                        }
                    }
                }
            }
        }

        // movement
        if (player.inputDevice.value) {
            player.v = player.speed * (1 - 0.1f * ((player == ball.owner) ? 1 : 0));
            player.a = player.inputDevice.angle;
        } else {
            player.v = 0;
            if (scene.settings.easyMode && ball.owner == player) {
                ball.v = 0;
                ball.s = 0;
            }
        }

        player.animationStandRun();
    }

    private void assistBallControl() {
        // "movement" below only refreshes player.a from the input device when there's active
        // input, and runs after this; read the input device directly so the lead point tracks
        // where the player is currently looking, not last frame's heading.
        float facingAngle = player.inputDevice.value ? player.inputDevice.angle : player.a;

        float targetX = player.x + EASY_MODE_LEAD_DISTANCE * EMath.cos(facingAngle);
        float targetY = player.y + EASY_MODE_LEAD_DISTANCE * EMath.sin(facingAngle);

        float distanceToTarget = EMath.dist(ball.x, ball.y, targetX, targetY);

        ball.a = EMath.aTan2(targetY - ball.y, targetX - ball.x);
        ball.v = EMath.max(player.v, Math.min(EASY_MODE_MAX_CATCH_UP_SPEED, EASY_MODE_CATCH_UP_RATE * distanceToTarget));
        ball.s = 0;
    }

    @Override
    State checkConditions() {

        if ((player.role == Player.Role.GOALKEEPER)
                && (player == player.team.lineup.get(0))
                && (player.team.near1 != player)
                && (player.inputDevice == player.ai)) {
            return fsm.stateKeeperPositioning;
        }

        // player fired
        if (player.inputDevice.fire1Down()) {

            // kick
            if (ball.owner == player) {
                if (player.v > 0 && ball.z < 8) {
                    player.kickAngle = player.a;
                    return fsm.stateKick;
                }
            }

            // head or tackle
            else if (player.ballDistance < 120 && player.ballIsApproaching()) {

                Vector3 ballPrediction = ball.prediction[Math.min(player.frameDistance, Const.BALL_PREDICTION - 1)];

                if (ballPrediction.z > 18) {
                    return fsm.stateHead;
                } else if (player.v > 0
                        && player.ballIsInFront()
                        && player.ballDistance > 12) {
                    return fsm.stateTackle;
                }
            } else {
                // release input device
                if (player.team.usesAutomaticInputDevice()) {
                    player.inputDevice = player.ai;
                }
            }
        }
        return null;
    }
}
