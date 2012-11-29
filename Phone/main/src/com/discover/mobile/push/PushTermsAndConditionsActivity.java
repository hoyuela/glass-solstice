package com.discover.mobile.push;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.Activity;
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
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail.VidStatus;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.discover.mobile.navigation.NavigationMenuRootActivity;
import com.xtify.sdk.api.XtifySDK;

@ContentView(R.layout.push_terms_and_conditions)
public class PushTermsAndConditionsActivity extends RoboActivity{

	@InjectView(R.id.accept_terms)
	private Button accept;
	
	@InjectView(R.id.decline_terms)
	private Button decline;
	
	private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	private static final String DECLINE = "P"; //$NON-NLS-1$
	
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.activity = this;
		
		//TODO: We may want to move this somewhere else depending on the speed of the app
		getRegistrationStatus();
		
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

	public void getRegistrationStatus() {
		// TODO Auto-generated method stub
		final AsyncCallbackAdapter<PushRegistrationStatusDetail> callback = new AsyncCallbackAdapter<PushRegistrationStatusDetail>(){
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				switch(messageErrorResponse.getHttpStatusCode()) {
				// TODO: For now nothing really needs to be handled here
				// The reason for this is because this is all done in the background 
				// with no implications with the UI. Proper practice is to handle all
				// the errors.
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;	
					default:
						return true;
				}
			}
			
			@Override
			public void success(final PushRegistrationStatusDetail value){
				if(value.vidStatus != VidStatus.MISSING){
					//TODO: Set a status somewhere
					startNextActivity();
				}
			}
		};
		
		new GetPushRegistrationStatus(activity, callback).submit();
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
		Intent intent = new Intent(this, NavigationMenuRootActivity.class);	
		startActivity(intent);
	}
}
