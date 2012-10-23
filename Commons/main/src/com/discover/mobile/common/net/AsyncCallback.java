package com.discover.mobile.common.net;

public interface AsyncCallback<V> {
	
	void success(V value);

	// TODO
	void failure(Throwable error);
	
}
