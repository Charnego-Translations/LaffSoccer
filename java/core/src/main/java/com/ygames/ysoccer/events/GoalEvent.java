package com.ygames.ysoccer.events;

import com.ygames.ysoccer.match.Goal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalEvent extends GameEvent {
    Goal.Type type;
}
