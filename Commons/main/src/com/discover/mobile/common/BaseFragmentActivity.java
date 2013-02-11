
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
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
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
public class BaseFragmentActivity extends SlidingFragmentActivity implements RoboContext, ErrorHandlerUi, AlertDialogParent{
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
	 * Reference to the dialog currently being displayed on top of this activity. Is set using setDialog();
	 */
	private AlertDialog mActiveDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final RoboInjector injector = RoboGuice.getInjector(this);
		this.eventManager = injector.getInstance(EventManager.class);
		injector.injectMembersWithoutViews(this);
		super.onCreate(savedInstanceState);
		this.eventManager.fire(new OnCreateEvent(savedInstanceState));
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
		this.eventManager.fire(new OnRestartEvent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.eventManager.fire(new OnStartEvent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		//Load all application and user preferences from persistent storage
		Globals.loadPreferences(this);

		this.eventManager.fire(new OnResumeEvent());

		//Set this activity as the active activity
		//FIXME
		//BankActivityManager.setActiveActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//Save all application and user preferences into persistent storage
		Globals.savePreferences(this);

		this.eventManager.fire(new OnPauseEvent());

		closeDialog();
	}


	@Override
	protected void onNewIntent( final Intent intent ) {
		super.onNewIntent(intent);
		this.eventManager.fire(new OnNewIntentEvent());
	}

	@Override
	protected void onStop() {
		try {
			this.eventManager.fire(new OnStopEvent());
		} finally {
			super.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			this.eventManager.fire(new OnDestroyEvent());
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
		this.eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		RoboGuice.getInjector(this).injectViewMembers(this);
		this.eventManager.fire(new OnContentChangedEvent());
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
	}

	@Override
	public Map<Key<?>, Object> getScopedObjectMap() {
		return this.scopedObjects;
	}

	/**
	 * Sets the fragment seen by the user
	 * @param fragment - fragment to be shown
	 */
	private void setVisibleFragment(final Fragment fragment) {
		this.currentFragment = fragment;
		getSupportFragmentManager()
		.beginTransaction()
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
	private void setVisibleFragmentNoHistory(final Fragment fragment) {
		this.currentFragment = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.navigation_content, fragment)
		.commit();
		hideSlidingMenuIfVisible();
	}

	/**
	 * Set the current fragment that is being shown
	 * @param fragment - fragment that is currently shown
	 */
	public void setCurrentFragment(final BaseFragment fragment){
		this.currentFragment = fragment;
	}

	/**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 */
	public void makeFragmentVisible(final Fragment fragment) {
		setVisibleFragment(fragment);
		hideSlidingMenuIfVisible();
	}

	/**
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 * @param addToHistory - boolean indicating if the fragment should be added to the back stack
	 */
	public void makeFragmentVisible(final Fragment fragment, final boolean addToHistory) {
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
	private void hideSlidingMenuIfVisible() {
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

//FIXME - determine if used
//	/**
//	 * A common method used to forward user to error modal dialog with a given static
//	 * string text message
//	 * 
//	 * @param errorCode HTTP error code
//	 * @param errorText Text that is displayed in the content area of dialog
//	 * @param titleText Text that is displayed at the top of the screen which describes the reason of the error
//	 */
//	public void sendToErrorPage(final int errorCode, final int titleText, final int errorText) {
//		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
//		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
//		startActivity(maintenancePageIntent);
//		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
//	}
//
//	/**
//	 * A common method used to forward user to error page with a given static
//	 * string text message
//	 * 
//	 * @param errorText
//	 */
//	public void sendToErrorPage(final int errorText) {
//		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
//		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
//		startActivity(maintenancePageIntent);
//	}

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
		this.mLastError = errorCode;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getLastError()
	 */
	@Override
	public int getLastError() {
		return this.mLastError;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		//FIXME
		//return BankErrorHandler.getInstance();
		return null;
	}

	/**
	 * @return Return a reference to the current dialog being displayed over this activity.
	 */
	@Override
	public AlertDialog getDialog() {
		return this.mActiveDialog;
	}

	/**
	 * Allows to set the current dialog that is being displayed over this activity.
	 */
	@Override
	public void setDialog(final AlertDialog dialog) {
		this.mActiveDialog = dialog;
	}

	/**
	 * Closes the current dialog this is being displayed over this activity. Requires
	 * a call to setDialog to be able to use this function.
	 */
	@Override
	public void closeDialog() {
		if( this.mActiveDialog != null && this.mActiveDialog.isShowing()) {
			this.mActiveDialog.dismiss();
			this.mActiveDialog = null;
		}
	}

	/**
	 * Starts a Progress dialog using this activity as the context. The ProgressDialog created
	 * will be set at the active dialog.
	 */
	@Override
	public void startProgressDialog() {
		if( this.mActiveDialog == null ) {
			this.mActiveDialog = ProgressDialog.show(this,"Discover", "Loading...", true);
			setDialog(this.mActiveDialog);
		}
	}
}
