package com.ygames.ysoccer.framework.commentary;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommentPriority {
    CHITCHAT(3), LOW(1), COMMON(2), HIGH(4), GOAL(5);
    public final int weight;
}
