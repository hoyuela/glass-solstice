package com.discover.mobile.common.net;

import android.os.Looper;

final class ThreadUtility {
	
	static boolean isMainThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	static void forceMainThreadExecution() {
		if(!isMainThread())
			throw new AssertionError("Should never be called outside the main thread");
	}
	
	private ThreadUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
