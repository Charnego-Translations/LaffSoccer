package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.StateId.END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.StateId.FINAL_CELEBRATION_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFinalCelebrationPositions extends MatchState {

    enum Step {POSITIONING, CELEBRATING, QUITTING}

    private int side;
    private Step step;
    private Team winner;
    private Team runnerUp;
    private int celebrationEndingTime;

    MatchStateFinalCelebrationPositions(MatchFsm fsm) {
        super(FINAL_CELEBRATION_POSITIONS, fsm);

        checkReplayKey = false;
        checkPauseKey = false;
    }

    @Override
    void setDisplayFlags() {
        scene.clearDisplayFlags();
    }

    @Override
    void entryActions() {
        super.entryActions();

        winner = scene.competition.getFinalWinner();
        side = (winner == scene.team[HOME]) ? -1 : 1;
        runnerUp = scene.competition.getFinalRunnerUp();
        positionLineups();
        scene.setPointOfInterest(0, side * GOAL_LINE);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            boolean move = scene.updatePlayers(false);

            switch (step) {
                case POSITIONING:
                    if (readyToCelebrate()) {
                        winner.setLineupState(STATE_FINAL_CELEBRATION);
                        step = Step.CELEBRATING;
                        EventManager.publish(new CelebrationEvent());
                    }
                    break;

                case CELEBRATING:
                    if (!move) {
                        celebrationEndingTime = scene.stateTimer;
                        step = Step.QUITTING;
                    }
                    break;
            }


            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }


    @Override
    SceneFsm.Action[] checkConditions() {
        if (step == Step.QUITTING && (scene.stateTimer - celebrationEndingTime > SECOND)) {
            return newAction(NEW_FOREGROUND, END_POSITIONS);
        }

        return checkCommonConditions();
    }

    private void positionLineups() {

        List<Player> winners = new ArrayList<>(winner.lineup);

        step = Step.POSITIONING;
        PlayerCompareByX playerComparator = new PlayerCompareByX();
        Collections.sort(winners, playerComparator);

        float tx = -8 * winners.size();
        float ty = side * GOAL_LINE / 2f;
        for (Player player : winners) {
            player.isActive = true;
            player.setState(STATE_REACH_TARGET);
            player.setTarget(tx, ty);
            tx += 16;
        }

        runnerUp.setLineupTarget(Const.TOUCH_LINE + 80, 0);
        runnerUp.setLineupState(STATE_OUTSIDE);
    }

    private boolean readyToCelebrate() {
        for (Player player : winner.lineup) {
            if (player.checkState(STATE_REACH_TARGET)) {
                return false;
            }
        }
        return true;
    }

    static class PlayerCompareByX implements Comparator<Player> {

        @Override
        public int compare(Player player1, Player player2) {
            return EMath.sgn(player1.x - player2.x);
        }
    }
}
