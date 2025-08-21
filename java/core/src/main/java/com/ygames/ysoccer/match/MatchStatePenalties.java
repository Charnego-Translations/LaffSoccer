package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTIES_KICK;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_PENALTY_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SENT_OFF;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenalties extends MatchState {

    private boolean move;

    MatchStatePenalties(MatchFsm fsm) {
        super(fsm);

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
        scene.displayWindVane = true;
        scene.displayPenaltiesScore = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        // swap penalty kicking team
        scene.penaltyKickingTeam = 1 - scene.penaltyKickingTeam;
        scene.team[scene.penaltyKickingTeam].setSide(1);
        scene.team[1 - scene.penaltyKickingTeam].setSide(-1);

        // add another round
        if (scene.penaltiesLeft(HOME) == 0 && scene.penaltiesLeft(AWAY) == 0) {
            scene.addPenalties(1);
        }

        scene.nextPenalty();

        scene.setPointOfInterest(0, scene.penalty.side * PENALTY_SPOT_Y);

        setPlayersTargetPositions();
        scene.penalty.kicker.setTarget(-40 * scene.penalty.side, scene.penalty.side * (PENALTY_SPOT_Y - 45));
        scene.penalty.keeper.setTarget(0, scene.penalty.side * (GOAL_LINE - 4));
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
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            scene.penalty.keeper.setState(STATE_KEEPER_PENALTY_POSITIONING);
            return newAction(NEW_FOREGROUND, STATE_PENALTIES_KICK);
        }

        return checkCommonConditions();
    }

    private void setPlayersTargetPositions() {

        for (int t = HOME; t <= AWAY; t++) {
            Team team = scene.team[t];
            int len = team.lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = team.lineupAtPosition(i);
                if (!player.checkState(STATE_SUBSTITUTED) && !player.checkState(STATE_SENT_OFF)) {
                    int side = 2 * t - 1;
                    player.tx = 18 * (-team.lineup.size() + 2 * i) + 8 * EMath.cos(70 * (player.number));
                    player.ty = -(i == 0 ? 300 : 100) + side * (15 + 5 * (i % 2)) + 8 * EMath.sin(70 * (player.number));
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }
    }
}
