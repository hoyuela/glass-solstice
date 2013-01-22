package com.discover.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.discover.mobile.common.Globals;
import com.discover.mobile.error.ErrorHandlerFactory;

/**
 * This is the base activity for any activity that wants to use the Action bar
 * that is not logged in. This will show the back button with the Discover logo.
 * 
 * @author jthornton
 * 
 */
public abstract class NotLoggedInRoboActivity extends SherlockActivity implements ErrorHandlerUi {

	/**
	 * Create the activity and show the action bar
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showActionBar();
	}
	
	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		return ErrorHandlerFactory.getInstance();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		//Set this activity as the active activity
		ErrorHandlerFactory.getInstance().setActiveActivity(this);
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

		back.setVisibility(View.INVISIBLE);
		logo.setVisibility(View.VISIBLE);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				goBack();
			}
		});
	}
	
	/**
	 * Set the title in the action bar to display text instead of the default discover image
	 */
	public void setActionBarTitle(final int stringResource) {
		//Hide the title image in the action bar.
		((ImageView)this.findViewById(R.id.action_bar_discover_logo)).setVisibility(View.GONE);
		
		//Show title text with string resource.
		final TextView titleText = (TextView)findViewById(R.id.logged_out_title_view);
		titleText.setText(this.getString(stringResource));
		titleText.setVisibility(View.VISIBLE);
		
	}

	/**
	 * Set the title in the action bar to display the title image.
	 */
	public void setActionBarTitleImageVisible() {
		//Hide the title image in the action bar.
		((ImageView)this.findViewById(R.id.action_bar_discover_logo)).setVisibility(View.VISIBLE);
		
		//Hide title text and reset text value.
		final TextView titleText = (TextView)findViewById(R.id.title_view);
		titleText.setText(this.getString(R.string.empty));
		titleText.setVisibility(View.GONE);
	}
	
	/**
	 * Function to be implemented by subclasses to return to previous screen that opened
	 * the currently displayed screen.
	 * 
	 */
	public abstract void goBack();
	

	@Override
	public void showCustomAlert(AlertDialog alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorCode, int titleText, int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}


}
