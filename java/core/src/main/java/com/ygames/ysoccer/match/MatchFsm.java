package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.InputDeviceList;

import static com.ygames.ysoccer.match.MatchFsm.StateId.INTRO;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

public class MatchFsm extends SceneFsm<Match, MatchState> {

    boolean matchCompleted;

    final BenchStatus benchStatus;
    final Vector2 throwInPosition;
    Team throwInTeam;
    Team cornerKickTeam;
    Team goalKickTeam;

    public enum StateId implements SceneState.Id {
        BENCH_ENTER,
        BENCH_EXIT,
        BENCH_FORMATION,
        BENCH_SUBSTITUTIONS,
        BENCH_TACTICS,
        CORNER_KICK,
        CORNER_STOP,
        END,
        END_POSITIONS,
        EXTRA_TIME_STOP,
        FINAL_CELEBRATION_POSITIONS,
        FINAL_CELEBRATION,
        FREE_KICK,
        FREE_KICK_STOP,
        FULL_EXTRA_TIME_STOP,
        FULL_TIME_STOP,
        GOAL,
        GOAL_KICK,
        GOAL_KICK_STOP,
        HALF_EXTRA_TIME_STOP,
        HALF_TIME_ENTER,
        HALF_TIME_POSITIONS,
        HALF_TIME_STOP,
        HALF_TIME_WAIT,
        HELP,
        HIGHLIGHTS,
        INTRO,
        KEEPER_STOP,
        KICK_OFF,
        MAIN,
        PAUSE,
        PENALTIES,
        PENALTIES_END,
        PENALTIES_KICK,
        PENALTIES_STOP,
        PENALTY_KICK,
        PENALTY_KICK_STOP,
        RED_CARD,
        REPLAY,
        STARTING_POSITIONS,
        THROW_IN,
        THROW_IN_STOP,
        YELLOW_CARD,
    }

    MatchFsm(Match match, InputDeviceList inputDevices) {
        super(match, inputDevices);

        benchStatus = new BenchStatus();
        throwInPosition = new Vector2();

        new MatchStateBenchEnter(this);
        new MatchStateBenchExit(this);
        new MatchStateBenchFormation(this);
        new MatchStateBenchSubstitutions(this);
        new MatchStateBenchTactics(this);
        new MatchStateCornerKick(this);
        new MatchStateCornerStop(this);
        new MatchStateEnd(this);
        new MatchStateEndPositions(this);
        new MatchStateExtraTimeStop(this);
        new MatchStateFinalCelebrationPositions(this);
        new MatchStateFinalCelebration(this);
        new MatchStateFreeKick(this);
        new MatchStateFreeKickStop(this);
        new MatchStateFullExtraTimeStop(this);
        new MatchStateFullTimeStop(this);
        new MatchStateGoal(this);
        new MatchStateGoalKick(this);
        new MatchStateGoalKickStop(this);
        new MatchStateHalfExtraTimeStop(this);
        new MatchStateHalfTimeEnter(this);
        new MatchStateHalfTimePositions(this);
        new MatchStateHalfTimeStop(this);
        new MatchStateHalfTimeWait(this);
        new MatchStateHelp(this);
        new MatchStateHighlights(this);
        new MatchStateIntro(this);
        new MatchStateKeeperStop(this);
        new MatchStateKickOff(this);
        new MatchStateMain(this);
        new MatchStatePause(this);
        new MatchStatePenalties(this);
        new MatchStatePenaltiesEnd(this);
        new MatchStatePenaltiesKick(this);
        new MatchStatePenaltiesStop(this);
        new MatchStatePenaltyKick(this);
        new MatchStatePenaltyKickStop(this);
        new MatchStateRedCard(this);
        new MatchStateReplay(this);
        new MatchStateStartingPositions(this);
        new MatchStateThrowIn(this);
        new MatchStateThrowInStop(this);
        new MatchStateYellowCard(this);
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, INTRO);
        pushAction(FADE_IN);
    }

    static class BenchStatus {
        Team team;
        InputDevice inputDevice;
        final Vector2 oldTarget = new Vector2();
        int selectedPosition;
        int substPosition = -1;
        int swapPosition = -1;
        int selectedTactics;
    }
}
