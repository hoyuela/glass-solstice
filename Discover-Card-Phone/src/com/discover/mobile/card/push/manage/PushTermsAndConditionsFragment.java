package com.discover.mobile.card.push.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.common.BaseFragment;

/**
 * The push terms and conditions screen. It shows the terms and conditions
 * to the user for the push notifications. 
 * 
 * @author jthornton
 *
 */
public class PushTermsAndConditionsFragment extends BaseFragment{

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
		
		final View view = inflater.inflate(R.layout.push_terms_and_conditions, null);
		return view;
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.manage_push_fragment_title;
	}
}
