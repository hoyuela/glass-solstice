package com.discover.mobile.common;

import android.os.Looper;

public final class ThreadUtility {
	
	public static boolean isMainThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	public static void assertMainThreadExecution() {
		if(!isMainThread())
			throw new AssertionError("Should never be called outside the main thread");
	}
	
	public static void assertCurrentThreadHasLooper() {
		if(Looper.myLooper() == null)
			throw new AssertionError("Current thread does not have an associated Looper, callbacks can't be scheduled");
	}
	
	private ThreadUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
