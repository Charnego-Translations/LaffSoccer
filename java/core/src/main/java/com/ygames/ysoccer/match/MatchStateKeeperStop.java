package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.MatchFsm.State.KEEPER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.State.MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateKeeperStop extends MatchState {

    private Player keeper;
    private Team keeperTeam;
    private Team opponentTeam;

    MatchStateKeeperStop(MatchFsm fsm) {
        super(KEEPER_STOP, fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayControlledPlayer = true;
        scene.displayBallOwner = true;
        scene.displayTime = true;
        scene.displayRadar = true;
        scene.displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        keeper = scene.ball.holder;
        keeperTeam = scene.team[keeper.team.index];
        opponentTeam = scene.team[1 - keeper.team.index];

        scene.stats[opponentTeam.index].overallShots += 1;
        scene.stats[opponentTeam.index].centeredShots += 1;

        keeperTeam.assignAutomaticInputDevices(keeper);
        opponentTeam.assignAutomaticInputDevices(null);

        keeperTeam.setPlayersState(STATE_REACH_TARGET, keeper);
        opponentTeam.setPlayersState(STATE_REACH_TARGET, null);

        keeperTeam.updateTactics(true);
        opponentTeam.updateTactics(true);

        scene.setPointOfInterest(keeper.x, keeper.y);
    }

    @Override
    void onResume() {
        super.onResume();

        scene.actionCamera
            .setMode(FOLLOW_BALL)
            .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;

        while (timeLeft > GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.ball.updatePrediction();
                scene.updateFrameDistance();
                scene.updateAi();
            }

            scene.updateBall();
            scene.updatePlayers(true);
            scene.findNearest();

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (keeper.checkState(STATE_STAND_RUN) || keeper.checkState(STATE_KEEPER_POSITIONING)) {
            keeperTeam.setPlayersState(STATE_STAND_RUN, keeper);
            opponentTeam.setPlayersState(STATE_STAND_RUN, null);
            return newAction(NEW_FOREGROUND, MAIN);
        }

        return checkCommonConditions();
    }
}
