package com.discover.mobile.push.manage;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;

/**
 * The success listener for the posting the preferences to the server
 * @author jthornton
 *
 */
public class PostPrefsSuccessListener implements SuccessListener <PostPreferencesDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private PushManageFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public PostPrefsSuccessListener(final PushManageFragment fragment){
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
	 * @param successObject - object retrieved from the server
	 * @return the successful response
	 */
	@Override
	public void success(final PostPreferencesDetail successObject) {
		fragment.hideSavebar();
		fragment.showSuccessSave();
	}

}
