package com.third.signala.transport;

import com.third.signala.ConnectionBase;
import com.third.signala.ConnectionState;
import com.third.signala.SendCallback;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StateBase {
    protected ConnectionBase mConnection;
    protected AtomicBoolean mIsRunning = new AtomicBoolean(false);
    
    public StateBase(ConnectionBase connection)
    {
        mConnection = connection;
    }

    public abstract ConnectionState getState();
    public abstract void Start();
    public abstract void Stop();

    public boolean getIsRunning() { return mIsRunning.get(); }
    public void Run()
    {
    	if (mIsRunning.compareAndSet(false, true)) {
            try
            {
                OnRun();
            }
            finally
            {
                mIsRunning.set(false);
            }
    	}
    }

    protected abstract void OnRun();

	public abstract void Send(CharSequence text, SendCallback callback);
}
