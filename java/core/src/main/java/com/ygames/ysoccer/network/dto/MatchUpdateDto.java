package com.ygames.ysoccer.network.dto;

import com.ygames.ysoccer.match.MatchFsm;

public class MatchUpdateDto {

    public int light;
    public BallUpdateDto ballUpdateDto;
    public TeamUpdateDto[] teamUpdateDto;
    public boolean displayTime;
    public boolean displayWindVane;
    public boolean displayRosters;
    public MatchFsm.StateId stateId;
    public int stateTimer;

    public MatchUpdateDto() {
    }
}
