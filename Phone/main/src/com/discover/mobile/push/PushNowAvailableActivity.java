package com.discover.mobile.push;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class PushNowAvailableActivity extends RoboSherlockFragment{
	
	//FIXME this needs to be removed from this class and be put somewhere else
	//private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	//FIXME this needs to be removed from this class and be put somewhere else
	//private static final String DECLINE = "P"; //$NON-NLS-1$
	
	@InjectView(R.id.manage_alerts_button)
	private Button manageAlerts;
	
	//FIXME this needs to be removed from this class and be put somewhere else
	//private Context context;

	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View view = inflater.inflate(R.layout.push_now_available, null);
		
		//this.context = view.getContext();
		
		manageAlerts.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				//TODO: Go to manage alerts screen
			}
		});
		
		return view;
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
