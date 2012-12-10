package com.discover.mobile.push;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.os.Bundle;

import com.discover.mobile.R;

@ContentView(R.layout.push_enroll)
public class PushEnrollActivity extends RoboActivity {
	
	//FIXME this needs to be removed from this class and be put somewhere else
	//private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	//FIXME this needs to be removed from this class and be put somewhere else
	//private static final String DECLINE = "P"; //$NON-NLS-1$


	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	//FIXME this needs to be removed from this class and be put somewhere else
//	protected void registerWithDiscover(final String regStatus){
//		final TelephonyManager telephonyManager =
//				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		
//		final DeviceRegistrationDetail detail = new DeviceRegistrationDetail();
//		detail.regStatus = regStatus;
//		detail.vid = XtifySDK.getXidKey(context);
//		detail.os = "Android"; //$NON-NLS-1$
//		detail.id = telephonyManager.getDeviceId();
//		final String version = Build.VERSION.RELEASE;
//		if(null != version){
//			detail.version = version;
//		}
//		
//		final AsyncCallback<DeviceRegistrationDetail> callback = 
//				GenericAsyncCallback.<DeviceRegistrationDetail>builder((Activity)context)
//				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
//									getResources().getString(R.string.push_progress_registration_loading), 
//									true)
//				.withErrorResponseHandler(new PushRegistrationErrorHandler())
//				.launchIntentOnSuccess(NavigationRootActivity.class)
//				.build();
//		
//		new RegisterVenderIdCall(context, callback, detail).submit();
//	}
}
