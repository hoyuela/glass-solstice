package com.discover.mobile;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * This is the base activity for any activity that wants to use the Action bar
 * that is not logged in. This will show the back button with the Discover logo.
 * 
 * @author jthornton
 * 
 */
public abstract class NotLoggedInRoboActivity extends SherlockActivity {

	/**
	 * Create the activity and show the action bar
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showActionBar();
	}

	/**
	 * Show the action bar with the custom layout
	 */
	public void showActionBar() {
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final ImageView logo = (ImageView) this
				.findViewById(R.id.action_bar_discover_logo);
		final ImageView back = (ImageView) this
				.findViewById(R.id.navigation_back_button);

		back.setVisibility(View.VISIBLE);
		logo.setVisibility(View.VISIBLE);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				goBack();
			}
		});
	}

	/**
	 * Function to be implemented by subclasses to return to previous screen that opened
	 * the currently displayed screen.
	 * 
	 */
	public abstract void goBack();

}
