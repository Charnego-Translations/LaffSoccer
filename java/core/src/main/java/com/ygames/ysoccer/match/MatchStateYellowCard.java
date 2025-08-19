package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_YELLOW_CARD;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateYellowCard extends MatchState {

    boolean booked;

    MatchStateYellowCard(MatchFsm matchFsm) {
        super(matchFsm);
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayFoulMaker = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        booked = false;

        scene.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        super.onResume();

        scene.setPointOfInterest(scene.foul.position);

        scene.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(scene.foul.player.x, scene.foul.player.y)
                .setSpeed(NORMAL)
                .setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        setPlayerStates();

        if (!booked && timer > SECOND) {
            scene.foul.player.setState(STATE_YELLOW_CARD);
            booked = true;
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            scene.updateBall();
            scene.ball.inFieldKeep();
            scene.ball.collisionGoal();
            scene.ball.collisionJumpers();
            scene.ball.collisionNetOut();
            scene.ball.collisionNet();

            scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (booked && scene.foul.player.checkState(STATE_IDLE)) {
            if (scene.referee.isSentOff(scene.foul.player)) {
                scene.foul.player.setState(STATE_SENT_OFF);
            }
            if (scene.foul.isPenalty()) {
                return newAction(NEW_FOREGROUND, STATE_PENALTY_KICK_STOP);
            } else {
                return newAction(NEW_FOREGROUND, STATE_FREE_KICK_STOP);
            }
        }
        return checkCommonConditions();
    }

    private void setPlayerStates() {
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = scene.team[t].lineup.get(i);
                PlayerState playerState = player.getState();
                if (playerState.checkId(STATE_STAND_RUN) || playerState.checkId(STATE_KEEPER_POSITIONING)) {
                    player.tx = player.x;
                    player.ty = player.y;
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }
    }
}
