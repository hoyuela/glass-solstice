package com.discover.mobile.bank.whatsnew;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankNetworkServiceCallManager;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * Activity for the what's new content.  This will be shown on the first time a user logs into the application.
 * After that, the user will not see this page again. This is meant to be reusable for future releases.
 * 
 * To updated this go to the strings_bank_whats_new.xml file and update the following:
 * 
 * bank_whats_new_drawables - Array containing the images to be displayed in the adapter.  The top
 * 							  reference is the left most image.
 * bank_whats_new_pages - Number of pages that will be displayed.  This should match the number of
 * 						  entries in the drawable array.
 * 
 * @author jthornton
 *
 */
public class WhatsNewActivity extends FragmentActivity implements ErrorHandlerUi, AlertDialogParent{

	/**Tag used for logging*/
	private static final String TAG = WhatsNewActivity.class.getSimpleName();

	/**Key to get the location of the view pager out of the bundle*/
	private static final String PAGER_LOCATION = "location";

	/**View pager holding the content that is displayed to the user*/
	private ViewPager mPager;

	/**List of ImageViews that are used to indicate what page the user is on*/
	private List<ImageView> indicatorsList;

	/**The currently selected ImageView*/
	private ImageView selected;

	/**Number of pages that will be displayed in the view pager*/
	private int numPages;

	/**The pager adapter, which provides the pages to the view pager widget.*/
	private WhatsNewViewPagerAdapter mPagerAdapter;

	/**Boolean set to true if the session is timing out*/
	private boolean isTimingOut = false;

	/**
	 * Create the activity. 
	 * @param savedInstanceState - saved state of the bundle
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Globals.setOldTouchTimeInMillis(Calendar.getInstance().getTimeInMillis());
		setContentView(R.layout.bank_whats_new_content_view);
		final ImageButton button = (ImageButton) findViewById(R.id.close);
		numPages = getResources().getInteger(R.integer.bank_whats_new_pages);
		int currentIndex = 0;
		mPager = (ViewPager) findViewById(R.id.pager);


		mPagerAdapter = new WhatsNewViewPagerAdapter(this, getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				onBackPressed();
			}
		});

		if(null != savedInstanceState){
			currentIndex = savedInstanceState.getInt(PAGER_LOCATION, 0);
		}

		setUpIndicators(numPages, currentIndex);
	}

	/**
	 * Set up the indicators at the bottom of the what's new screen
	 * @param numPages - number of pages that the view pager has
	 * @param selectedIndex - the selected index of the view pager
	 */
	private void setUpIndicators(final int numPages, final int selectedIndex){
		final Resources res = getResources();
		final LinearLayout indicators = (LinearLayout) findViewById(R.id.page_indicators);
		final int margin = res.getDimensionPixelSize(R.dimen.bank_gray_circle_margin);
		final int imageSize = res.getDimensionPixelSize(R.dimen.bank_gray_circle_size);
		indicatorsList = new ArrayList<ImageView>();
		for(int i = 0; i < numPages; i++){
			final ImageView image = new ImageView(this);
			final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
			params.setMargins(margin, 0, margin, 0);
			if(selectedIndex == numPages - 1 - i){
				selected = image;
				image.setBackgroundResource(R.drawable.bank_dark_gray_circle);
			}else{
				image.setBackgroundResource(R.drawable.bank_light_gray_circle);
			}
			indicators.addView(image, params);
			indicatorsList.add(image);
		}
	}

	/**
	 * Resume the activity.
	 */
	@Override
	public void onResume(){
		super.onResume();

		//Set this activity as the active activity
		DiscoverActivityManager.setActiveActivity(this);

		/**
		 * Set the on page change listener.  This needs to be done here
		 * so that when the view pager resumes its state it won't call the
		 * onPageSelected method (which causes improper highlighting).
		 */
		mPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(final int arg0) {}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {}

			@Override
			public void onPageSelected(final int pageNumber) {
				indicatorsList.get(numPages - pageNumber - 1).setBackgroundResource(R.drawable.bank_dark_gray_circle);
				selected.setBackgroundResource(R.drawable.bank_light_gray_circle);
				selected = indicatorsList.get(numPages - 1 - pageNumber);
			}
		});
	}

	/**
	 * Used to handle user interaction across the application.
	 * 
	 * @param ev
	 *            The MotionEvent that was recognized.
	 * @return True if consumed, false otherwise.
	 */
	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		super.dispatchTouchEvent(ev);

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			compareLastTouchTimeAndUpdateSession();
		}

		// Don't consume event.
		return false;
	}

	/**
	 * Determines the current time and gets the time stored in globals. Then
	 * updates globals with the current time.
	 */
	private void compareLastTouchTimeAndUpdateSession() {
		final Calendar mCalendarInstance = Calendar.getInstance();

		final long previousTime = Globals.getOldTouchTimeInMillis();
		final long currentTime = mCalendarInstance.getTimeInMillis();

		if(!setIsUserTimedOut(previousTime, currentTime)) {
			KeepAlive.checkForRequiredSessionRefresh();
		}
		Globals.setOldTouchTimeInMillis(currentTime);
	}


	/**
	 * Determines whether or not the user is timed out.
	 * 
	 * @param previousTime
	 * @param currentTime
	 * @return true if the user is timed-out, false otherwise.
	 */
	private boolean setIsUserTimedOut(final long previousTime,
			final long currentTime) {
		// Previous value exists
		if (previousTime != 0) {
			final int oneSecond = 1000;
			final long difference = currentTime - previousTime;
			final float secs = (float) difference / oneSecond;

			// User has become inactive and will be set to timed-out.
			if ( secs > BankUrlManager.MAX_IDLE_TIME) {
				isTimingOut = true;
				BankConductor.logoutUser(this);
				return true;
			}
		}
		return false;
	}

	/**
	 * Save the state of the activity.
	 * @param outState - bundle to save the state the activity
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		outState.putInt(PAGER_LOCATION, mPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Handle when the back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		if(!isTimingOut){
			BankConductor.navigateToHomePage();
		}
	}

	@Override
	public TextView getErrorLabel() {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
		return null;
	}

	@Override
	public void showCustomAlert(final AlertDialog alert) {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText) {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content,
			final int buttonText) {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void setLastError(final int errorCode) {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
	}

	@Override
	public int getLastError() {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
		return 0;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		/**
		 * Intentionally left blank.  The only service call that is made here is the logout, which the
		 * result, if an error occurs, gets ignored. 
		 */		
		return null;
	}

	/**
	 * @return Return a reference to the current dialog being displayed over this activity.
	 */
	@Override
	public AlertDialog getDialog() {
		return DiscoverModalManager.getActiveModal();
	}

	/**
	 * Allows to set the current dialog that is being displayed over this activity.
	 */
	@Override
	public void setDialog(final AlertDialog dialog) {
		DiscoverModalManager.setActiveModal(dialog);
	}

	/**
	 * Closes the current dialog this is being displayed over this activity. Requires
	 * a call to setDialog to be able to use this function.
	 */
	@Override
	public void closeDialog() {
		if( DiscoverModalManager.hasActiveModal() && DiscoverModalManager.isAlertShowing()) {
			DiscoverModalManager.clearActiveModal();
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Activity does not have a dialog associated with it!" );
			}
		}
	}

	/**
	 * Starts a Progress dialog using this activity as the context. The ProgressDialog created
	 * will be set at the active dialog.
	 */
	@Override
	public void startProgressDialog(final boolean progressDialogIsCancelable) {		
		if(!DiscoverModalManager.hasActiveModal()) {
			DiscoverModalManager.setActiveModal(ProgressDialog.show(DiscoverActivityManager.getActiveActivity(), 
					"Discover", "Loading...", true));
			DiscoverModalManager.setProgressDialogCancelable(progressDialogIsCancelable);
			DiscoverModalManager.getActiveModal().setCanceledOnTouchOutside(false);
			DiscoverModalManager.getActiveModal().setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(final DialogInterface dialog) {
					onCancelProgressDialog();
				}
			});
			DiscoverModalManager.setAlertShowing(true);
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Activity does not have a dialog associated with it!" );
			}
		}
	}

	/**
	 * When the progress dialog is dismiss cancel the service call.
	 */
	public void onCancelProgressDialog() {
		BankNetworkServiceCallManager.getInstance().cancelServiceCall();
	}
}
