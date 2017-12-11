package com.third.signala;

public abstract class SendCallback {
	public abstract void OnSent(CharSequence messageSent);
	public abstract void OnError(Exception ex);
}
