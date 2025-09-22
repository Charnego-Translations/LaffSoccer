package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.WARP;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;
import static com.ygames.ysoccer.match.Match.HOME;

public class MatchCamera extends ActionCamera {

    Match match;
    Goal goal;

    public MatchCamera(Match match) {
        super(match.getBall());
        this.match = match;
    }

    @Override
    public void updateSettings() {
        switch (match.state.getId()) {
            case INTRO:
                mode = match.state.timer > SECOND ? FOLLOW_BALL : STILL;
                speed = NORMAL;
                break;

            case STARTING_POSITIONS:
            case KICK_OFF:
            case HALF_TIME_ENTER:
                mode = FOLLOW_BALL;
                speed = FAST;
                offsetX = 0;
                offsetY = 0;
                break;

            case MAIN:
            case CORNER_STOP:
            case GOAL_KICK_STOP:
            case KEEPER_STOP:
            case THROW_IN_STOP:
            case HALF_TIME_STOP:
            case FULL_TIME_STOP:
            case EXTRA_TIME_STOP:
            case HALF_EXTRA_TIME_STOP:
            case FULL_EXTRA_TIME_STOP:
            case PENALTIES_STOP:
                mode = FOLLOW_BALL;
                speed = NORMAL;
                xLimited = true;
                yLimited = true;
                break;

            case HALF_TIME_POSITIONS:
            case HALF_TIME_WAIT:
            case END_POSITIONS:
            case END:
                mode = REACH_TARGET;
                speed = FAST;
                target.set(0, 0);
                break;

            case CORNER_KICK:
            case GOAL_KICK:
            case THROW_IN:
            case PENALTY_KICK:
                mode = FOLLOW_BALL;
                speed = FAST;
                offsetX = 0;
                offsetY = 0;
                xLimited = true;
                yLimited = true;
                break;

            case BENCH_ENTER:
                mode = REACH_TARGET;
                speed = WARP;
                target.set(-0.55f * TOUCH_LINE, -20);
                xLimited = false;
                yLimited = true;
                break;

            case BENCH_SUBSTITUTIONS:
            case BENCH_FORMATION:
            case BENCH_TACTICS:
            case FREE_KICK_STOP:
            case PENALTIES_KICK:
            case PENALTIES_END:
                mode = STILL;
                break;

            case BENCH_EXIT:
                mode = REACH_TARGET;
                speed = WARP;
                target.set(match.fsm.benchStatus.oldTarget);
                break;

            case FREE_KICK:
                mode = FOLLOW_BALL;
                speed = FAST;
                offsetX = -match.foul.position.x / 10f;
                offsetY = -80 * match.foul.opponent.team.side;
                xLimited = true;
                yLimited = true;
                break;

            case GOAL:
                if (match.state.timer < SECOND) {
                    mode = FOLLOW_BALL;
                    speed = NORMAL;
                } else {
                    mode = REACH_TARGET;
                    speed = FAST;
                    goal = match.goals.get(match.goals.size() - 1);
                    target.set(goal.player.x, goal.player.y);
                }
                xLimited = false;
                yLimited = false;
                break;

            case YELLOW_CARD:
            case RED_CARD:
                mode = REACH_TARGET;
                speed = NORMAL;
                target.set(match.foul.player.x, match.foul.player.y);
                xLimited = true;
                yLimited = true;
                break;

            case PENALTY_KICK_STOP:
                mode = REACH_TARGET;
                speed = NORMAL;
                target.set(0, match.penalty.side * PENALTY_SPOT_Y);
                xLimited = true;
                yLimited = true;
                break;

            case PENALTIES:
                mode = REACH_TARGET;
                speed = NORMAL;
                target.set(0, -PENALTY_SPOT_Y);
                xLimited = true;
                yLimited = true;
                break;

            case FINAL_CELEBRATION:
                mode = REACH_TARGET;
                speed = NORMAL;
                target.set(0, winnerSide() * GOAL_LINE / 1.25f);
                xLimited = false;
                yLimited = false;
                break;
        }
    }

    private int winnerSide() {
        return (match.competition.getFinalWinner() == match.team[HOME]) ? -1 : 1;
    }
}
