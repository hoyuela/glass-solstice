package com.discover.mobile.push;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * This is the fragment that allows the user to enroll in push alerts.  This is will be displayed when the user goes
 * through profile and settings and then clicks on enroll in reminders.  If the user clicks enroll then the app
 * will register the acceptance of the notification with Discover's server.  If the user clicks cancel it will just 
 * go back to the last fragment displayed.
 * 
 * @author jthornton
 *
 */
public class PushEnrollFragment extends BasePushRegistrationUI{
	
	/**String representing this class to enter into the back stack*/
	private static final String TAG = PushEnrollFragment.class.getSimpleName();

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
		
		final View view = inflater.inflate(R.layout.push_enroll, null);
		final Button enroll = (Button) view.findViewById(R.id.enroll_accept);
		final TextView cancel = (TextView) view.findViewById(R.id.enroll_decline);
		
		enroll.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				registerWithDiscover(ACCEPT, true);
			}
		});
		
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				changeToDeclineScreen();
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
	 * Swap out this fragment for the last fragment displayed in the app
	 */
	@Override
	public void changeToDeclineScreen() {
		this.getActivity().onBackPressed();
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.enroll_fragment_title;
	}
}
