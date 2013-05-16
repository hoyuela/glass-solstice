
package com.discover.mobile.common;

import java.util.List;

import roboguice.activity.RoboActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * Base class for all SherlockActivities for our app
 * 
 * @author ekaram
 *
 */
public abstract class BaseActivity extends RoboActivity 
	implements ErrorHandlerUi, AlertDialogParent, SyncedActivity {

	private static final String TAG = BaseActivity.class.getSimpleName();
	/**
	 * Contains the last error that occurred with the activity. 
	 * An object that holds a reference to an instance of BaseActivity can set its value by using setLastError.
	 */
	private int mLastError = 0;
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
	 * Show a custom modal alert dialog for the activity
	 * @param alert - the modal alert to be shown
	 */
	@Override
	public void showCustomAlert(final AlertDialog alert){
		DiscoverModalManager.setActiveModal(alert);
		DiscoverModalManager.setAlertShowing(true);
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
		
		KeepAlive.checkForRequiredSessionRefresh();
		
		return false;
	}

	/**
	 * Show the default one-button alert with a custom title, content an button text
	 * 
	 * Uses the orange button
	 * 
	 * @param title - the resource id for title for the alert
	 * @param content - the resource id for content to display on the box
	 * @param buttonText - the resource id for button text to display on the button
	 */
	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(this,title,content,buttonText));
	}

	/**
	 * Show the default one-button alert with a custom title, content an button text
	 * 
	 * Uses the orange button
	 * 
	 * @param title - the resource id for title for the alert
	 * @param content - the resource id for content to display on the box
	 * @param buttonText - the resource id for button text to display on the button
	 */
	@Override
	public void showDynamicOneButtonAlert(final int title, final String content, final int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(this,title,content,buttonText));
	}


	/* 
	 * Child classes should override this to implement error handling behavior
	 * (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
	 */
	@Override
	public TextView getErrorLabel() {
		return null;
	}

	/*
	 * Child classes should override this to implement error handling behavior
	 *  (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
	 */
	@Override
	public List<EditText> getInputFields(){
		return null;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getContext()
	 */
	@Override
	public Context getContext() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#setLastError()
	 */
	@Override
	public void setLastError(final int errorCode) {
		mLastError = errorCode;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getLastError()
	 */
	@Override
	public int getLastError() {
		return mLastError;
	}


	@Override
	public void onResume(){
		super.onResume();

		//Load all application and user preferences from persistent storage
		Globals.loadPreferences(this);

		//Set this activity as the active activity
		DiscoverActivityManager.setActiveActivity(this);

		//If a modal was showing show the modal
		if(DiscoverModalManager.isAlertShowing() && null != DiscoverModalManager.getActiveModal()){
			DiscoverModalManager.getActiveModal().show();
			DiscoverModalManager.setAlertShowing(true);
		}
		
		/**
		 * Unlocks any thread blocking on waitForResume() 
		 */
		notifyResumed();
	}

	/**
	 * 
	 */
	@Override
	public void onPause() {
		/**Reset flag to detect if activity is in it's resumed state*/
		resumed = false;
		
		super.onPause();

		//Save all application and user preferences into persistent storage
		Globals.savePreferences(this);

		//Close the modal if it is showing
		if(DiscoverModalManager.hasActiveModal()){
			DiscoverModalManager.getActiveModal().dismiss();
			DiscoverModalManager.setAlertShowing(true);
		}else{
			DiscoverModalManager.clearActiveModal();
		}

		closeDialog();
	}

	@Override
	public void onBackPressed() {
		/**Clear any modal that may have been created during the life of this activity*/
		DiscoverModalManager.clearActiveModal();
		
		super.onBackPressed();
	}
	
	@Override
	public void startActivity (final Intent intent) {
		/**Clear any modal that may have been created during the life of the current fragment*/
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
	 * To be implemented by the child class
	 */
	@Override
	public abstract ErrorHandler getErrorHandler();


	/**
	 * @return Return a reference to the current dialog being displayed over this activity.
	 */
	@Override
	public AlertDialog getDialog() {
		return mActiveDialog;
	}

	/**
	 * Allows to set the current dialog that is being displayed over this activity.
	 */
	@Override
	public void setDialog(final AlertDialog dialog) {
		mActiveDialog = dialog;

	}

	/**
	 * Closes the current dialog this is being displayed over this activity. Requires
	 * a call to setDialog to be able to use this function.
	 */
	@Override
	public void closeDialog() {
		if( mActiveDialog != null && mActiveDialog.isShowing()) {
			mActiveDialog.dismiss();
			mActiveDialog = null;
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
	public void startProgressDialog() {		
		if( mActiveDialog == null ) {
			mActiveDialog = ProgressDialog.show(this,"Discover", "Loading...", true);	
			setDialog(mActiveDialog);
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Activity does not have a dialog associated with it!" );
			}
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


