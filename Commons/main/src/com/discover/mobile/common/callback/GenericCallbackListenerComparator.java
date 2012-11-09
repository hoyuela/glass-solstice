package com.discover.mobile.common.callback;

import java.util.Comparator;

class GenericCallbackListenerComparator implements Comparator<GenericCallbackListener> {

	@Override
	public int compare(final GenericCallbackListener lhs, final GenericCallbackListener rhs) {
		return lhs.getOrder().compareTo(rhs.getOrder());
	}
	
}
