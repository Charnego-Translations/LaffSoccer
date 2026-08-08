package com.ygames.ysoccer.match;

public class Referee {

    public enum PenaltyCard {YELLOW, RED, DOUBLE_YELLOW, YELLOW_PLUS_RED}

    void addYellowCard(Player player) {
        player.penaltyCard = player.hasYellowCard() ? PenaltyCard.DOUBLE_YELLOW : PenaltyCard.YELLOW;
    }
}
