package com.discover.mobile.push;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.xtify.sdk.api.XtifySDK;


//TODO:  Java doc this as part of another story.  This class will most likely change
@ContentView(R.layout.push_now_available)
public class PushNowAvailableActivity extends RoboActivity{
	
	private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	private static final String DECLINE = "P"; //$NON-NLS-1$
	
	@InjectView(R.id.manage_alerts_button_accept)
	private Button accept;
	
	@InjectView(R.id.manage_alerts_button_decline)
	private Button decline;

	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		accept.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				registerWithDiscover(ACCEPT);
			}		
		});
		
		decline.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				registerWithDiscover(DECLINE);
			}		
		});
	}
	
	protected void registerWithDiscover(final String regStatus){
		final TelephonyManager telephonyManager =
				(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		final DeviceRegistrationDetail detail = new DeviceRegistrationDetail();
		detail.regStatus = regStatus;
		detail.vid = XtifySDK.getXidKey(this);
		detail.os = "Android"; //$NON-NLS-1$
		detail.id = telephonyManager.getDeviceId();
		final String version = Build.VERSION.RELEASE;
		if(null != version){
			detail.version = version;
		}
		
		final AsyncCallback<DeviceRegistrationDetail> callback = GenericAsyncCallback.<DeviceRegistrationDetail>builder(this)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withErrorResponseHandler(new PushRegistrationErrorHandler())
				.launchIntentOnSuccess(NavigationRootActivity.class)
				.build();
		
		new RegisterVenderIdCall(this, callback, detail).submit();
	}
}
