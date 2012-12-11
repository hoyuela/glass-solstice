package com.discover.mobile.push;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.xtify.sdk.api.XtifySDK;

/**
 * The base class for any piece of UI that is going to register the vendor id with Discover's server
 * @author jthornton
 *
 */
public abstract class BasePushRegistrationUI extends RoboSherlockFragment implements PushRegistrationUI{
		
	/**String representing that the user opted into the alerts*/
	public static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	/**String representing that the user did not want to opt into the alerts*/
	public static final String DECLINE = "P"; //$NON-NLS-1$

	/**
	 * Registers the device, user and vender id with Discover's server
	 * @param regStatus - string representing if the user accepted the terms.
	 */
	protected void registerWithDiscover(final String regStatus){
		final Context context = this.getActivity();
		final TelephonyManager telephonyManager =
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		final boolean isOptedIn = (ACCEPT.equals(regStatus)) ? true : false;
		final DeviceRegistrationDetail detail = new DeviceRegistrationDetail();
		detail.regStatus = regStatus;
		detail.vid = XtifySDK.getXidKey(context);
		detail.os = "Android"; //$NON-NLS-1$
		detail.id = telephonyManager.getDeviceId();
		final String version = Build.VERSION.RELEASE;
		if(null != version){
			detail.version = version;
		}
		
		final AsyncCallback<DeviceRegistrationDetail> callback = 
				GenericAsyncCallback.<DeviceRegistrationDetail>builder((Activity)context)
				.showProgressDialog(context.getResources().getString(R.string.push_progress_get_title), 
									context.getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PushRegisterSuccessListener(this, isOptedIn))
				.withErrorResponseHandler(new PushRegisterErrorHandler(this, isOptedIn))
				.build();
		
		new RegisterVenderIdCall(context, callback, detail).submit();
	}
	
	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	public void changeToAcceptScreen(final String tag) {
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new PushManageFragment())
		.addToBackStack(tag)
		.commit();
	}
}
