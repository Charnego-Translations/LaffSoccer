package com.ygames.ysoccer.match;

public class Referee {

    public enum PenaltyCard {YELLOW, RED, DOUBLE_YELLOW, YELLOW_PLUS_RED}

    void addYellowCard(Player player) {
        player.penaltyCard = hasYellowCard(player) ? PenaltyCard.DOUBLE_YELLOW : PenaltyCard.YELLOW;
    }

    void addRedCard(Player player) {
        player.penaltyCard = hasYellowCard(player) ? PenaltyCard.YELLOW_PLUS_RED : PenaltyCard.RED;
    }

    boolean hasYellowCard(Player player) {
        return player.penaltyCard == PenaltyCard.YELLOW;
    }

    boolean isSentOff(Player player) {
        return player.penaltyCard != null && player.penaltyCard != PenaltyCard.YELLOW;
    }
}
