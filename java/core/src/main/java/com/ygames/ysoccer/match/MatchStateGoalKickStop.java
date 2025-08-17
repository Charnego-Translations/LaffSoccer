package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_GOAL_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoalKickStop extends MatchState {

    private final Vector2 goalKickPosition = new Vector2();

    MatchStateGoalKickStop(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayTime = true;
        scene.displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        scene.resetAutomaticInputDevices();
        scene.setPlayersState(STATE_REACH_TARGET, null);

        Team goalKickTeam = scene.team[1 - scene.ball.ownerLast.team.index];
        Player goalKickKeeper = goalKickTeam.lineup.get(0);
        goalKickKeeper.tx = scene.ball.x / 4;
        goalKickKeeper.ty = goalKickTeam.side * (Const.GOAL_LINE - 8);

        Team opponentTeam = scene.team[1 - goalKickTeam.index];
        Player opponentKeeper = opponentTeam.lineup.get(0);
        opponentKeeper.tx = 0;
        opponentKeeper.ty = opponentTeam.side * (Const.GOAL_LINE - 8);

        goalKickTeam.updateTactics(true);
        opponentTeam.updateTactics(true);

        goalKickPosition.set(
                (Const.GOAL_AREA_W / 2f) * scene.ball.xSide,
                (Const.GOAL_LINE - Const.GOAL_AREA_H) * scene.ball.ySide
        );
    }

    @Override
    void onResume() {
        scene.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL)
                .setLimited(true, true);

        scene.setPointOfInterest(goalKickPosition);
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

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((scene.ball.v < 5) && (scene.ball.vz < 5)) {
            scene.ball.setPosition(goalKickPosition.x, goalKickPosition.y, 0);
            scene.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_GOAL_KICK);
        }

        return checkCommonConditions();
    }
}
