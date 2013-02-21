package com.discover.mobile.common.callback;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Used as a task to lock and unlock the screen for orientation change during an 
 * ongoing network service call. An instance of this class is passed to a
 * GenericAsyncCallback instance via it's builder.withCompletionListener prior to 
 * calling submit. This will lock the screen in its current orientation. Once
 * the network service call has completed the orientation change will be unlocked.
 * 
 *
 * In the following example PreAuthCheckCall is a sub class of NetworkServiceCall. 
 * An instance of LockScreenCompletion listener is passed to GenericAsyncCallback's builder. 
 * On the LockScreenCompletionListener's instance creation it locks the screen in its current 
 * orientaiton. When Pre-AuthCheckCall calls submit it will send  out an http request. Upon 
 * failing or receiving a response to the http request, it will call LockScreenCompletionListener.complete 
 * method, which will allow the device to change orientation once again.
 * 
 * final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback.<PreAuthResult> builder(this)
 *				.withSuccessListener(new PreAuthSuccessResponseHandler(this))
 *				.withErrorResponseHandler(new PreAuthErrorResponseHandler(this))
 *				.withCompletionListener(new LockScreenCompletionListener(this)).build();
 *
 *		new PreAuthCheckCall(this, callback).submit();
 *		
 * @author henryoyuela
 *
 */
public class LockScreenCompletionListener implements CompletionListener {
	private final Activity activity;
	
	public LockScreenCompletionListener(final Activity activityToLock) {
		activity = activityToLock;
//		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);	
	}
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.LAST;
	}

	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object arg0) {
		if( null != activity ) {
//			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}
	
	public void setScreenOrientation(Activity a){
		int orientation = 0;
		if (null != a){
			if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			}else {
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
		}
		activity.setRequestedOrientation(orientation);
	}

}

