package com.discover.mobile.card.push.history;

import com.discover.mobile.card.services.push.history.PostReadDetail;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Success listener for when the application contacts the server letting it know a notification was read
 * @author jthornton
 *
 */
public class ReadNotificationSucessListener implements SuccessListener<PostReadDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private final PushHistoryItem item;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public ReadNotificationSucessListener(final PushHistoryItem item){
		this.item = item;
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
	public void success(final NetworkServiceCall<?> sender, final PostReadDetail detail) {
		item.setItemRead();
	}
}