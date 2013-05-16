
package com.discover.mobile.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.google.inject.Key;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Base class for anything that is going to use the sliding fragment or the action bar.
 * This also provides a subset of of methods that can be used.
 * 
 * @author jthornton
 *
 */
public abstract class BaseFragmentActivity extends SlidingFragmentActivity 
implements RoboContext, ErrorHandlerUi, AlertDialogParent, SyncedActivity{

	private static final String TAG = BaseFragmentActivity.class.getSimpleName();
	/**
	 * Contains the last error that occurred with the activity.
	 * An object that holds a reference to an instance of BaseActivity can set its value by using setLastError.
	 */
	private int mLastError = 0;

	/**Fragment that is currently being shown to the user*/
	protected Fragment currentFragment;

	protected EventManager eventManager;
	protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();
	/**
	 * Flag used to determine if the activity is in resumed state
	 */
	private boolean resumed = false;
	/**
	 * lock used to synchronize with threads attempting to update activity
	 */
	private static final Object lock = new Object();

	/**
	 * Reference to the dialog currently being displayed on top of this activity. Is set using setDialog();
	 */
	private AlertDialog mActiveDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final RoboInjector injector = RoboGuice.getInjector(this);
		eventManager = injector.getInstance(EventManager.class);
		injector.injectMembersWithoutViews(this);
		super.onCreate(savedInstanceState);
		eventManager.fire(new OnCreateEvent(savedInstanceState));
	}

	/**
	 * Set the title in the action bar for display
	 * @param title - title to show in the display
	 */
	public void setActionBarTitle(final String title){
		final TextView titleView= (TextView)findViewById(R.id.title_view);
		titleView.setText(title);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		eventManager.fire(new OnRestartEvent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventManager.fire(new OnStartEvent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		//Load all application and user preferences from persistent storage
		Globals.loadPreferences(this);

		eventManager.fire(new OnResumeEvent());

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

	@Override
	protected void onPause() {
		/**Reset flag to detect if activity is in it's resumed state*/
		resumed = false;

		super.onPause();

		//Save all application and user preferences into persistent storage
		Globals.savePreferences(this);

		eventManager.fire(new OnPauseEvent());

		closeDialog();

		//Close the modal if it is showing
		if(DiscoverModalManager.hasActiveModal()){
			DiscoverModalManager.getActiveModal().dismiss();
			DiscoverModalManager.setAlertShowing(true);
		}else{
			DiscoverModalManager.clearActiveModal();
		}
	}


	@Override
	protected void onNewIntent( final Intent intent ) {
		super.onNewIntent(intent);
		eventManager.fire(new OnNewIntentEvent());
	}

	@Override
	protected void onStop() {
		try {
			eventManager.fire(new OnStopEvent());
		} finally {
			super.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			eventManager.fire(new OnDestroyEvent());
		} finally {
			try {
				RoboGuice.destroyInjector(this);
			} finally {
				super.onDestroy();
			}
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		final Configuration currentConfig = getResources().getConfiguration();
		super.onConfigurationChanged(newConfig);
		eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		RoboGuice.getInjector(this).injectViewMembers(this);
		eventManager.fire(new OnContentChangedEvent());
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
	}

	@Override
	public Map<Key<?>, Object> getScopedObjectMap() {
		return scopedObjects;
	}

	/**
	 * Sets the fragment seen by the user
	 * @param fragment - fragment to be shown
	 */
	protected void setVisibleFragment(final Fragment fragment) {

		currentFragment = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.setCustomAnimations(R.anim.fragment_slide_in_right , R.anim.fragment_slide_out_left, R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right)
		.replace(R.id.navigation_content, fragment)
		//Adds the class name and fragment to the back stack
		.addToBackStack(fragment.getClass().getSimpleName())
		.commit();

		hideSlidingMenuIfVisible();
	}

	/**
	 * Sets the fragment seen by the user, but does not add it to the history
	 * @param fragment - fragment to be shown
	 */
	protected void setVisibleFragmentNoHistory(final Fragment fragment) {
		currentFragment = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.setCustomAnimations(R.anim.fragment_slide_in_right , R.anim.fragment_slide_out_left, R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right)
		.replace(R.id.navigation_content, fragment)
		.commit();

		hideSlidingMenuIfVisible();
	}

	/**
	 * Set the current fragment that is being shown
	 * @param fragment - fragment that is currently shown
	 */
	public void setCurrentFragment(final BaseFragment fragment){
		currentFragment = fragment;
	}

	/**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 */
	public void makeFragmentVisible(final Fragment fragment) {
		/**Clear any modal that may have been created during the life of the current fragment*/
		DiscoverModalManager.clearActiveModal();

		setVisibleFragment(fragment);
		hideSlidingMenuIfVisible();
	}

	/**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 * @param addToHistory - boolean indicating if the fragment should be added to the back stack
	 */
	public void makeFragmentVisible(final Fragment fragment, final boolean addToHistory) {
		/**Clear any modal that may have been created during the life of the current fragment*/
		DiscoverModalManager.clearActiveModal();

		if(addToHistory){
			setVisibleFragment(fragment);
		}else{
			setVisibleFragmentNoHistory(fragment);
		}
		hideSlidingMenuIfVisible();
	}

	/**
	 * Hides the sliding menu is it is currently visible
	 */
	public void hideSlidingMenuIfVisible() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		if(slidingMenu.isBehindShowing()) {
			slidingMenu.showAbove();
		}
	}

	/**
	 * Go back to the previous screen
	 */
	public void goBack(){
		onBackPressed();
	}

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

	/**
	 * Child class to provide proper error handler
	 */
	@Override
	public abstract ErrorHandler getErrorHandler() ;

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
		/**Clear any modal that may have been created during the life of the current activity*/
		DiscoverModalManager.clearActiveModal();

		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Utility method used for debugging issues in the back stack
	 */
	public void printFragmentsInBackStack() {

		final FragmentManager fragManager = this.getSupportFragmentManager();
		final int fragCount = fragManager.getBackStackEntryCount();
		if (fragCount > 0) {
			for (int i = 0; i < fragCount; i++) {
				Log.v(TAG, fragManager.getBackStackEntryAt(i).getName());
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
