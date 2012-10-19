package com.discover.mobile.common.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class NetworkTrafficExecutorHolder {
	
	// TODO consider externalizing if there's a good reason
	private static final int NETWORK_THREAD_COUNT = 3;
	
	static final ExecutorService networkTrafficExecutor;
	
	static {
		networkTrafficExecutor = createNetworkTrafficExecutor();
	}
	
	private static ExecutorService createNetworkTrafficExecutor() {
		return Executors.newFixedThreadPool(NETWORK_THREAD_COUNT);
	}
	
	private NetworkTrafficExecutorHolder() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
