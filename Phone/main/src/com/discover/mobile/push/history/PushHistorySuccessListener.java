package com.discover.mobile.push.history;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.history.NotificationListDetail;

public class PushHistorySuccessListener implements SuccessListener<NotificationListDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private PushHistoryFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public PushHistorySuccessListener(final PushHistoryFragment fragment){
		this.fragment = fragment;
	}

	/**
	 * Get the callback priority of the success handler
	 * @return the callback priority of the success handler
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the successful response
	 * @param detail - object retrieved from the server
	 * @return the successful response
	 */
	@Override
	public void success(final NotificationListDetail detail) {
		fragment.addToList(detail);
	}
}