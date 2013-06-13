package com.discover.mobile.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.SimpleContentModal;

/**
 * This is the base activity for any activity that wants to use the Action bar
 * that is not logged in. This will show the back button with the Discover logo.
 * 
 * @author jthornton
 * 
 */
public abstract class NotLoggedInRoboActivity extends SherlockActivity 
implements ErrorHandlerUi, AlertDialogParent, SyncedActivity {

	private static final String TAG = NotLoggedInRoboActivity.class.getSimpleName();

	protected boolean modalIsPresent = false;
	/**
	 * Reference to the dialog currently being displayed on top of this activity. Is set using setDialog();
	 */
	private AlertDialog mActiveDialog;
	/**
	 * Flag used to determine if the activity is in resumed state
	 */
	private boolean resumed = false;
	/**
	 * lock used to synchronize with threads attempting to update activity
	 */
	private static final Object lock = new Object();

	/**
	 * Create the activity and show the action bar
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showActionBar();
	}

	/**
	 * Child class must supply proper error handler
	 */
	@Override
	public abstract ErrorHandler getErrorHandler();


	@Override
	public void onResume(){
		super.onResume();

		DiscoverActivityManager.setActiveActivity(this);

		//If a modal was showing show the modal
		//If a modal was showing show the modal
		if(DiscoverModalManager.isAlertShowing() && null != DiscoverModalManager.getActiveModal()){
			if (DiscoverModalManager.getActiveModal() instanceof ProgressDialog) {
				startProgressDialog(false);
			} else {
				DiscoverModalManager.getActiveModal().show();
			}
			DiscoverModalManager.setAlertShowing(true);
		}

		/**
		 * Unlocks any thread blocking on waitForResume() 
		 */
		notifyResumed();
	}

	@Override
	public void onPause() {
		/**Reset flag to detect if activity is in it's resumed state*/
		resumed = false;

		super.onPause();
		//closeDialog();

		if(DiscoverModalManager.hasActiveModal()){
			if (DiscoverModalManager.getActiveModal() instanceof ProgressDialog) {
				DiscoverModalManager.getActiveModal().dismiss();
			} else {
				DiscoverModalManager.getActiveModal().hide();
			}
			DiscoverModalManager.setAlertShowing(true);
		}else{
			DiscoverModalManager.clearActiveModal();
		}
	}

	@Override
	public void onBackPressed() {
		/**Clear any modal that may have been created during the life of the current activity*/
		DiscoverModalManager.clearActiveModal();

		super.onBackPressed();
	}

	@Override
	public void startActivity (final Intent intent) {
		/**Clear any modal that may have been created during the life of the current activity*/
		DiscoverModalManager.clearActiveModal();

		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult (final Intent intent, final int requestCode) {
		/**Clear any modal that may have been created during the life of the current fragment*/
		DiscoverModalManager.clearActiveModal();

		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Show the action bar with the custom layout
	 */
	public void showActionBar() {
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final ImageView logo = (ImageView) findViewById(R.id.action_bar_discover_logo);
		final ImageView back = (ImageView) findViewById(R.id.navigation_back_button);

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
		((ImageView)findViewById(R.id.action_bar_discover_logo)).setVisibility(View.GONE);

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
		((ImageView)findViewById(R.id.action_bar_discover_logo)).setVisibility(View.VISIBLE);

		//Hide title text and reset text value.
		final TextView titleText = (TextView)findViewById(R.id.title_view);
		titleText.setText(this.getString(R.string.empty));
		titleText.setVisibility(View.GONE);
	}

	/**
	 * Present a modal error dialog over the current activity with a given title and body text. Can also close the
	 * current activity on close if needed.
	 * 
	 * @param titleText - the String resource to present in the title of the modal dialog.
	 * @param bodyText - the String resource to present in the body of the modal dialog.
	 * @param finishActivityOnClose - if passed as true, the activity that displays the modal 
	 * error will be finished when the modal is closed.
	 */
	protected void showErrorModal(final int titleText, final int bodyText, final boolean finishActivityOnClose) {
		final Activity activity = this;

		final SimpleContentModal errorModal =  new SimpleContentModal(activity);

		errorModal.setTitle(activity.getResources().getString(titleText));
		errorModal.setContent(activity.getResources().getString(bodyText));

		errorModal.showErrorIcon(true);
		errorModal.setButtonText(R.string.close_text);

		errorModal.getHelpFooter().setToDialNumberOnClick(R.string.need_help_number_text);


		if(finishActivityOnClose){
			errorModal.finishActivityOnClose(activity);
		}

		errorModal.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modalIsPresent = false;
				errorModal.dismiss();
				if(finishActivityOnClose){
					goBack();
				}
			}
		});

		errorModal.setOnCancelListener(new OnCancelListener() {			
			@Override
			public void onCancel(final DialogInterface dialog) {
				if(finishActivityOnClose){
					goBack();
				}else{
					errorModal.dismiss();
				}
			}
		});

		modalIsPresent = true;
		showCustomAlert(errorModal);

	}

	/**
	 * Present a modal error dialog over the current activity with a given title and body text. Can also close the
	 * current activity on close if needed.
	 * 
	 * @param titleText - the String resource to present in the title of the modal dialog.
	 * @param bodyText - the String resource to present in the body of the modal dialog.
	 * @param finishActivityOnClose - if passed as true, the activity that displays the modal 
	 * error will be finished when the modal is closed.
	 */
	protected void showErrorModalForRegistration(final int titleText, final int bodyText, final boolean finishActivityOnClose) {
		final Activity activity = this;

		final SimpleContentModal errorModal = new SimpleContentModal(activity);

		errorModal.setTitle(activity.getResources().getString(titleText));
		errorModal.setContent(activity.getResources().getString(bodyText));

		errorModal.showErrorIcon(true);
		errorModal.setButtonText(R.string.close_text);

		errorModal.getHelpFooter().setToDialNumberOnClick(R.string.need_help_number_text);


		if(finishActivityOnClose){
			errorModal.finishActivityOnClose(activity);
		}

		errorModal.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modalIsPresent = false;
				errorModal.dismiss();
				if(finishActivityOnClose){
					goBack();
				}
			}
		});

		errorModal.setOnCancelListener(new OnCancelListener() {			
			@Override
			public void onCancel(final DialogInterface dialog) {
				if(finishActivityOnClose){
					goBack();
				}else{
					errorModal.dismiss();
				}
			}
		});

		modalIsPresent = true;
		errorModal.show();

	}
	/**
	 * Function to be implemented by subclasses to return to previous screen that opened
	 * the currently displayed screen.
	 * 
	 */
	public abstract void goBack();


	@Override
	public void showCustomAlert(final AlertDialog alert) {
		DiscoverModalManager.setActiveModal(alert);
		DiscoverModalManager.setAlertShowing(true);

		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content,
			final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(final int errorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
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
	public void startProgressDialog(boolean isProgressDialogCancelable) {		
		if( !DiscoverModalManager.hasActiveModal() ) {
			DiscoverModalManager.setActiveModal(ProgressDialog.show(this,"Discover", "Loading...", true));
			DiscoverModalManager.setAlertShowing(true);
		}
	}

	@Override
	public boolean isReady() {
		return resumed;
	}

	@Override
	public boolean waitForResume(final int millis) {
		synchronized (lock) {
			/**
			 * If activity is not resumed then wait for it to resume, this wait can be unlocked
			 * via notifyResumed() which is called in the onResume of this activity.
			 */
			if( !isReady() ) {	
				try {
					if( millis >= 0 ) {
						lock.wait(millis);
					} else {
						lock.wait();
					}
				} catch (final InterruptedException e) {
					if( Log.isLoggable(TAG, Log.ERROR)) {
						Log.e(TAG,"An error occurred while waiting for activity to resume");
					}
				}
			} else {
				if( Log.isLoggable(TAG, Log.WARN)) {
					Log.v(TAG,"Activity is Ready!");
				}
			}
		} 

		return isReady();
	}

	/**
	 * Method utilize to unblock any thread blocking on waitForResume
	 */
	private void notifyResumed() {	
		synchronized (lock) {
			resumed = true;

			lock.notifyAll();
		}
	}
}
