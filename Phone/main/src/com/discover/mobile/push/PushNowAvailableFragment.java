package com.discover.mobile.push;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;
import com.discover.mobile.common.push.registration.RegisterVenderIdCall;
import com.discover.mobile.section.home.HomeSummaryFragment;
import com.xtify.sdk.api.XtifySDK;

/**
 * This is the screen that is showed to the user immediately following the login of the activity.
 * This will only be shown if the user has not chosen or denied the use of push notifications. If the user
 * has chosen to deny or opt into the alerts then this will not be shown and the user will be forwarded to
 * the account home.
 * 
 * @author jthornton
 *
 */
public class PushNowAvailableFragment extends RoboSherlockFragment{
	
	/**String representing this class to enter into the back stack*/
	private static final String TAG = PushNowAvailableFragment.class.getSimpleName();
	
	/**String representing that the user opted into the alerts*/
	private static final String ACCEPT = "Y"; //$NON-NLS-1$
	
	/**String representing that the user did not want to opt into the alerts*/
	private static final String DECLINE = "P"; //$NON-NLS-1$
	
	/**
	 * Creates the fragment, inflates the view and defines the button functionality.
	 * @param inflater - inflater that will inflate the layout
	 * @param container - container that will hold the views
	 * @param savedInstanceState - bundle containing information about the previous state of the fragment
	 * @return the inflated view
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View view = inflater.inflate(R.layout.push_now_available, null);
		final Button manageAlerts = (Button) view.findViewById(R.id.manage_alerts_button);
		final TextView accountHome = (TextView) view.findViewById(R.id.account_home_view);
		
		manageAlerts.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				registerWithDiscover(ACCEPT);
			}
		});
		
		accountHome.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				registerWithDiscover(DECLINE);
			}
		});

		return view;
	}
	
	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	protected void changeToPushManageScreen(){
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new PushManageFragment())
		.addToBackStack(TAG)
		.commit();
	}
	
	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	protected void changeAccountHomeScreen(){
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new HomeSummaryFragment())
		.addToBackStack(TAG)
		.commit();
	}
	
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
}
