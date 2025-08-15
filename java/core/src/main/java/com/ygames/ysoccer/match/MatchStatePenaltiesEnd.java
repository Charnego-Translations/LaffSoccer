package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.PenaltyState.MISSED;
import static com.ygames.ysoccer.match.Match.PenaltyState.SCORED;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTIES;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltiesEnd extends MatchState {

    private boolean goalLineCrossed;
    private boolean isGoal;
    private Player keeper;

    MatchStatePenaltiesEnd(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;
        displayPenaltiesScore = true;

        checkBenchCall = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
    }

    @Override
    void entryActions() {
        super.entryActions();

        goalLineCrossed = false;
        isGoal = false;
        keeper = scene.team[1 - scene.penaltyKickingTeam].lineupAtPosition(0);

        scene.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        super.onResume();

        scene.actionCamera.setMode(STILL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
                scene.updateFrameDistance();
            }

            scene.updateBall();
            if (!goalLineCrossed && !isGoal
                && scene.ball.y * scene.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)
                && EMath.isIn(scene.ball.x, -Const.POST_X, Const.POST_X)
                && (scene.ball.z <= Const.CROSSBAR_H)) {
                isGoal = true;
                Assets.Sounds.homeGoal.play(Assets.Sounds.volume / 100f);
            }

            if (scene.ball.y * scene.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)) {
                goalLineCrossed = true;
            }

            // if ball crosses the goal line or comes back, keeper has nothing more to do
            if (goalLineCrossed || EMath.sin(scene.ball.a) > 0) {
                if (keeper.checkState(STATE_KEEPER_POSITIONING)) {
                    keeper.setState(STATE_IDLE);
                }
            }

            // if keeper catches the ball, has nothing more to do
            if (scene.ball.holder == keeper) {
                if (keeper.checkState(STATE_KEEPER_KICK_ANGLE)) {
                    keeper.setState(STATE_IDLE);
                }
            }

            scene.ball.collisionGoal();
            scene.ball.collisionNet();

            scene.updatePlayers(true);

            if ((scene.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                scene.ball.updatePrediction();
            }

            scene.nextSubframe();

            scene.save();

            scene.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if ((scene.ball.v == 0) && (scene.ball.vz == 0)) {
            if (isGoal) {
                scene.penalty.setState(SCORED);
            } else {
                scene.penalty.setState(MISSED);
            }

            if (timer > 3 * SECOND) {

                scene.ball.setPosition(0, -Const.PENALTY_SPOT_Y, 0);
                scene.ball.updatePrediction();

                if (haveWinner()) {
                    scene.setResult(scene.penaltiesScore(HOME), scene.penaltiesScore(AWAY), Match.ResultType.AFTER_PENALTIES);
                    fsm.matchCompleted = true;

                    if (scene.competition.getFinalWinner() != null) {
                        return newAction(NEW_FOREGROUND, STATE_FINAL_CELEBRATION);
                    } else {
                        return newAction(NEW_FOREGROUND, STATE_END_POSITIONS);
                    }
                } else {
                    return newAction(NEW_FOREGROUND, STATE_PENALTIES);
                }
            }
        }

        return checkCommonConditions();
    }

    private boolean haveWinner() {
        // 1) home team cannot be reached
        if (scene.penaltiesScore(HOME) > scene.penaltiesPotentialScore(AWAY)) return true;

        // 2) away team cannot be reached
        if (scene.penaltiesScore(AWAY) > scene.penaltiesPotentialScore(HOME)) return true;

        // 3) all penalties have been kicked and score is not the same
        return scene.penaltiesLeft(HOME) == 0
            && scene.penaltiesLeft(AWAY) == 0
            && scene.penaltiesScore(HOME) != scene.penaltiesScore(AWAY);
    }
}
