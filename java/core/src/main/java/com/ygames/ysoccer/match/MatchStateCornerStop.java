package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.events.CornerKickEvent;
import com.ygames.ysoccer.events.WhistleEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.CORNER_KICK;
import static com.ygames.ysoccer.match.MatchFsm.StateId.CORNER_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateCornerStop extends MatchState {

    private final Vector2 cornerPosition = new Vector2();

    MatchStateCornerStop(MatchFsm fsm) {
        super(CORNER_STOP, fsm);

    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (scene.team[HOME].side == -scene.ball.ySide) {
            scene.stats[HOME].cornersWon += 1;
        } else {
            scene.stats[AWAY].cornersWon += 1;
        }

        EventManager.publish(new WhistleEvent());

        cornerPosition.set((Const.TOUCH_LINE - 12) * scene.ball.xSide, (Const.GOAL_LINE - 12) * scene.ball.ySide);

        // set the player targets relative to corner zone
        // even before moving the ball itself
        scene.ball.updateZone(cornerPosition.x, cornerPosition.y);
        scene.updateTeamTactics();
        scene.team[HOME].lineup.get(0).setTarget(0, scene.team[HOME].side * (Const.GOAL_LINE - 8));
        scene.team[AWAY].lineup.get(0).setTarget(0, scene.team[AWAY].side * (Const.GOAL_LINE - 8));

        EventManager.publish(new CornerKickEvent());

        scene.resetAutomaticInputDevices();

        scene.setPlayersState(STATE_REACH_TARGET, null);
    }

    @Override
    void onResume() {
        super.onResume();

        scene.setPointOfInterest(cornerPosition);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

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

            scene.updatePlayers(true);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((scene.ball.v < 5) && (scene.ball.vz < 5)) {
            scene.ball.setPosition(cornerPosition);
            scene.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, CORNER_KICK);
        }

        return checkCommonConditions();
    }
}
