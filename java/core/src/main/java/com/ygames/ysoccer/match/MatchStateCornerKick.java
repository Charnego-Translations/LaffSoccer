package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.SoundManager;

import static com.ygames.ysoccer.match.MatchFsm.StateId.CORNER_KICK;
import static com.ygames.ysoccer.match.MatchFsm.StateId.MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateCornerKick extends MatchState {

    private Player cornerKickPlayer;
    private boolean isKicking;

    MatchStateCornerKick(MatchFsm fsm) {
        super(CORNER_KICK, fsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
        scene.displayScore = true;
    }

    @Override
    void entryActions() {
        super.entryActions();
        if (scene.settings.commentary) {
            int size = Assets.Commentary.cornerKick.size();
            if (size > 0) {
                Assets.Commentary.cornerKick.get(Assets.random.nextInt(size)).play(SoundManager.volume / 100f);
            }
        }
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        fsm.cornerKickTeam.updateFrameDistance();
        fsm.cornerKickTeam.findNearest();
        cornerKickPlayer = fsm.cornerKickTeam.near1;

        cornerKickPlayer.setTarget(scene.ball.x + 7 * scene.ball.xSide, scene.ball.y);
        cornerKickPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();
        scene.updateTeamTactics();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            scene.updateBall();
            scene.ball.inFieldKeep();

            move = scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            EventManager.publish(new WhistleEvent());

            cornerKickPlayer.setState(STATE_CORNER_KICK_ANGLE);
            if (cornerKickPlayer.team.usesAutomaticInputDevice()) {
                cornerKickPlayer.inputDevice = cornerKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (scene.ball.v > 0) {
            scene.setPlayersState(STATE_STAND_RUN, cornerKickPlayer);
            return newAction(NEW_FOREGROUND, MAIN);
        }

        return checkCommonConditions();
    }
}
