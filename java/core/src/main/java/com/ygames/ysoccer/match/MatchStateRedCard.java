package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.StateId.PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.StateId.RED_CARD;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_RED_CARD;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateRedCard extends MatchState {

    boolean booked;

    MatchStateRedCard(MatchFsm matchFsm) {
        super(RED_CARD, matchFsm);
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

        EventManager.publish(new WhistleEvent());

        booked = false;

        scene.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        super.onResume();

        scene.setPointOfInterest(scene.foul.position);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        setPlayerStates();

        if (!booked && scene.stateTimer > SECOND) {
            scene.foul.player.setState(STATE_RED_CARD);
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

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (booked && scene.foul.player.checkState(STATE_IDLE)) {
            scene.foul.player.setState(STATE_SENT_OFF);
            if (scene.foul.isPenalty()) {
                return newAction(NEW_FOREGROUND, PENALTY_KICK_STOP);
            } else {
                return newAction(NEW_FOREGROUND, FREE_KICK_STOP);
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
