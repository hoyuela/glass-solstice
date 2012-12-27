package com.discover.mobile.push.manage;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;

/**
 * The success listener for the getting of the preferences to the server
 * @author jthornton
 *
 */
public class GetPushPrefsSuccessListener implements SuccessListener<PushNotificationPrefsDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private PushManageFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public GetPushPrefsSuccessListener(final PushManageFragment fragment){
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
	public void success(final PushNotificationPrefsDetail detail) {
		fragment.setPrefs(detail);
		fragment.displayPrefs();
	}
}
