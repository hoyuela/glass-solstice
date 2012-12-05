package com.discover.mobile.push;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.xtify.sdk.api.XtifySDK;

/**
 * The push terms and conditions screen. It shows the terms and conditions
 * to the user for the push notifications.  Currently is also waits for the user to 
 * accept or decline.  On that decision the app will then register the Discover either
 * way.
 * 
 * @author jthornton
 *
 */
@ContentView(R.layout.push_terms_and_conditions)
public class PushTermsAndConditionsActivity extends RoboActivity{

	@InjectView(R.id.accept_terms)
	private Button accept;
	
	@InjectView(R.id.decline_terms)
	private Button decline;
	
	private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	private static final String DECLINE = "P"; //$NON-NLS-1$

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		accept.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				registerWithDiscover(ACCEPT);
			}		
		});
		
		decline.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
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
		
		//FIXME: change this to the new way
		final AsyncCallbackAdapter<DeviceRegistrationDetail> callback = new AsyncCallbackAdapter<DeviceRegistrationDetail>(){
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				// FIXME handle the errors
				switch(messageErrorResponse.getMessageStatusCode()){
					default:
						return true;
				}
			}
			
			@Override
			public void success(DeviceRegistrationDetail value){
				startNextActivity();
			}
		};
				
		new RegisterVenderIdCall(this, callback, detail).submit();
	}
	
	protected void startNextActivity(){
		Intent intent = new Intent(this, NavigationRootActivity.class);	
		startActivity(intent);
	}
}
