package com.simpleentity.log;

import com.simpleentity.log.LogFile.State;

public interface LogFileListener {

	public void stateChanged(State previousState, State newState);

}
