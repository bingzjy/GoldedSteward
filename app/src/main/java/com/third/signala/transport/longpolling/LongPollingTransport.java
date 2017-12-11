package com.third.signala.transport.longpolling;

import com.third.signala.ConnectionBase;
import com.third.signala.transport.ITransport;
import com.third.signala.transport.StateBase;

public class LongPollingTransport implements ITransport {

	@Override
	public StateBase CreateInitialState(ConnectionBase connection) {
		return new DisconnectedState(connection);
	}

}
