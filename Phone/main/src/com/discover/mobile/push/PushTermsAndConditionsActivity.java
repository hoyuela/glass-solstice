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
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.xtify.sdk.api.XtifySDK;

/**
 * The push terms and conditions screen. It shows the terms and conditions
 * to the user for the push notifications. Currently is also waits for the user to 
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
		
				.withErrorResponseHandler(new ErrorResponseHandler(){

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public boolean handleFailure(final ErrorResponse<?> errorResponse) {
						// FIXME handle the errors
						switch(errorResponse.getHttpStatusCode()){
							default:
								return true;
						}
					}
					
				})
				.launchIntentOnSuccess(NavigationRootActivity.class)
				.build();
		
		new RegisterVenderIdCall(this, callback, detail).submit();
	}
}
