package com.discover.mobile.common;

import android.os.Looper;

public final class ThreadUtility {
	
	public static boolean isMainThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	public static void assertMainThreadExecution() {
		if(!isMainThread())
			throw new UnsupportedOperationException("Should never be called outside the main thread");
	}
	
	public static void assertMainThreadExecution(final Throwable causeIfThrown) {
		if(!isMainThread())
			throw new UnsupportedOperationException("Should never be called outside the main thread", causeIfThrown);
	}
	
	public static void assertNonMainThreadExecution() {
		if(isMainThread())
			throw new UnsupportedOperationException("Should never be called on the main thread");
	}
	
	public static void assertCurrentThreadHasLooper() {
		if(Looper.myLooper() == null)
			throw new UnsupportedOperationException(
					"Current thread does not have an associated Looper, callbacks can't be scheduled");
	}
	
	private ThreadUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
