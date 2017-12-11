package com.third.signala.transport.longpolling;

import com.third.signala.ConnectionBase;
import com.third.signala.ConnectionState;
import com.third.signala.SendCallback;
import com.third.signala.transport.StateBase;

import java.util.concurrent.atomic.AtomicBoolean;

public class DisconnectedState extends StateBase {
	private AtomicBoolean requestStart = new AtomicBoolean(false);
	
	public DisconnectedState(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Disconnected;
	}

	@Override
	public void Start() {
		requestStart.set(true);
		Run();
	}

	@Override
	public void Stop() {
	}

	@Override
	public void Send(CharSequence text, SendCallback callback) {
		callback.OnError(new Exception("Not connected"));
	}

	@Override
	protected void OnRun() {
		if(requestStart.get())
		{
	        NegotiatingState s = new NegotiatingState(mConnection);
	        mConnection.SetNewState(s);
		}
	}

}
