package com.ygames.ysoccer.match;

import com.ygames.ysoccer.events.CelebrationEvent;
import com.ygames.ysoccer.framework.EventManager;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.MatchFsm.StateId.END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.StateId.FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFinalCelebration extends MatchState {

    boolean move;

    public MatchStateFinalCelebration(MatchFsm matchFsm) {
        super(FINAL_CELEBRATION, matchFsm);

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

        scene.competition.getFinalWinner().setLineupState(STATE_FINAL_CELEBRATION);

        EventManager.publish(new CelebrationEvent());
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (scene.subframe % GLGame.SUBFRAMES == 0) {
                scene.updateAi();
            }

            move = scene.updatePlayers(false);

            scene.nextSubframe();

            scene.save();

            scene.camera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move && (scene.stateTimer > SECOND)) {
            return newAction(NEW_FOREGROUND, END_POSITIONS);
        }

        return checkCommonConditions();
    }
}
