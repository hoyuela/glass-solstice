package com.discover.mobile.push;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.section.home.HomeSummaryFragment;

/**
 * This is the screen that is showed to the user immediately following the login of the activity.
 * This will only be shown if the user has not chosen or denied the use of push notifications. If the user
 * has chosen to deny or opt into the alerts then this will not be shown and the user will be forwarded to
 * the account home.
 * 
 * @author jthornton
 *
 */
public class PushNowAvailableFragment extends BasePushRegistrationUI{
	
	/**String representing this class to enter into the back stack*/
	private static final String TAG = PushNowAvailableFragment.class.getSimpleName();
	
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
				registerWithDiscover(DECLINE, true);
			}
		});
		
		accountHome.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				registerWithDiscover(DECLINE, false);
			}
		});

		return view;
	}

	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	@Override
	public void changeToAcceptScreen() {
		super.changeToAcceptScreen(TAG);
	}

	/**
	 * Swap out this fragment and replace it with the push manage fragment so that the user can manage his/her alerts
	 */
	@Override
	public void changeToDeclineScreen() {
		this.getSherlockActivity().getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, new HomeSummaryFragment())
		.addToBackStack(TAG)
		.commit();
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.manage_push_fragment_title;
	}
}
