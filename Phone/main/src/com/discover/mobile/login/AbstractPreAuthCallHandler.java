package com.discover.mobile.login;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.R;
import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.alert.ModalDefaultOneButtonBottomView;
import com.discover.mobile.alert.ModalDefaultTopView;
import com.discover.mobile.common.ScreenType;


/**
 * AbstractPreAuthCallHandler
 * 
 * This is the abstract super class of the success and error response parsers for preauth.
 * This class handles the reading and writing of the dates to the preferences file so that we can check
 * when an optional update is required (30 days since last seen)
 * 
 * @author scottseward
 *
 */
public abstract class AbstractPreAuthCallHandler extends RoboSlidingFragmentActivity {
	
	protected Activity activity;
	
	private AlertDialog.Builder alertBuilder;
	
	private static final String UPGRADE_TITLE = "Upgrade";
	private static final String FORCED_UPGRADE_MESSAGE =
			"Your Discover app is out of date. You must update before continuing.";
	
	protected static final String DATETIME_KEY = "com.discover.mobile.optionalupdatedate";

	protected static final int NO_DATE = 0;
	protected static final int OPTIONAL_UPGRADE = 1;
	protected static final int FORCED_UPGRADE = 2;
	protected static final int DOES_NOT_EXIST = 0;
	
	protected static final long DAY_IN_MILLISECONDS = 86400000;
	protected static final int OPTIONAL_UPGRADE_DAYS = 30;
	protected static final String PACKAGE_NAME = "com.discover.mobile.DiscoverMobileActivity";
	
	protected static final String PREFS_FILE = "UpdatePreferences";

	/**
	 * Send a user to a maintenance page - this is similar to a popup dialog but shows a discover
	 * themed window that presents the user with an error message.
	 */
	protected final void sendToMaintenancePage() {
		final Intent maintenancePageIntent = new Intent(activity, LockOutUserActivity.class);
		ScreenType.UNSCHEDULED_MAINTENANCE.addExtraToIntent(maintenancePageIntent);
		activity.startActivity(maintenancePageIntent);
	}
	
	/**
	 * Shows the optional upgrade message to the user.
	 * This message is cancelable with no consequences. The user also has the option to press
	 * upgrade, which will take them to the Google Play store and the Discover app page.
	 * 
	 * @param message The message to be presented in the alert dialog.
	 */
	protected final void showOptionalUpgradeAlertDialog(final String message) {
		ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(activity, null);
		ModalDefaultOneButtonBottomView singleButtonBottomView = new ModalDefaultOneButtonBottomView(activity, null);
		
		titleAndContentForDialog.setTitle(R.string.upgrade_dialog_title);
		titleAndContentForDialog.setContent(R.string.optional_upgrade_dialog_body);
		
		singleButtonBottomView.setButtonText(R.string.upgrade_dialog_button_text);
				
		ModalAlertWithOneButton optionalUpgradeDialog = 
				new ModalAlertWithOneButton(activity, titleAndContentForDialog, singleButtonBottomView);
		
		singleButtonBottomView.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { upgrade(); }
		});
		
		showAlert(optionalUpgradeDialog);
	}
	
	/**
	 * showForcedUpgradeAlertDialog()
	 * This method, when called, shows an alert dialog with the forced upgrade text.
	 * This dialog needs to prevent the user from using the application.
	 * If the user chooses upgrade, they are directed to the Google Play store page for the Discover application.
	 * If the user cancels the dialog by pressing the back button or otherwise, the application is force quit.
	 */
	protected final void showForcedUpgradeAlertDialog() {
		ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(activity, null);
		ModalDefaultOneButtonBottomView singleButtonBottomView = new ModalDefaultOneButtonBottomView(activity, null);
		
		titleAndContentForDialog.setTitle(R.string.upgrade_dialog_title);
		titleAndContentForDialog.setContent(R.string.forced_upgrade_dialog_body);
		titleAndContentForDialog.showErrorIcon(true);
		
		singleButtonBottomView.setButtonText(R.string.upgrade_dialog_button_text);
				
		ModalAlertWithOneButton optionalUpgradeDialog = 
				new ModalAlertWithOneButton(activity, titleAndContentForDialog, singleButtonBottomView);
		
		singleButtonBottomView.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { upgrade(); }
		});
		optionalUpgradeDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
		@Override
		public void onCancel(DialogInterface dialog) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		});	
		
		showAlert(optionalUpgradeDialog);
	}
	
	/**
	 * shouldPresentOptionalUpdate()
	 * @param updateDescription The upgrade message to be displayed in the dialog.
	 * @return 
	 */
	protected final boolean shouldPresentOptionalUpdate(final String updateDescription) {
		if(updateDescription != null) {
			final SharedPreferences prefs = activity.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
			
			final long savedDate = prefs.getLong(DATETIME_KEY, DOES_NOT_EXIST);
			
			if (savedDate == DOES_NOT_EXIST) {
				final SharedPreferences.Editor editor = prefs.edit();
				editor.putLong(DATETIME_KEY, new Date().getTime());
				editor.commit();
				return true;
			}
			
			final long currentDate = new Date().getTime();
			final long daysDifference = (currentDate - savedDate)/DAY_IN_MILLISECONDS;
			
			if(daysDifference >= OPTIONAL_UPGRADE_DAYS) {
				updateDateInPrefs();
				return true;
			}
			
			return false;
		}
		
		return false;
	}

	/**
	 * Sends the user to the Google Play page for the Discover app.
	 * Used when a user wants or needs to upgrade their application.
	 */
	protected void upgrade() {
		final Uri marketUri = Uri.parse("market://details?id=" + PACKAGE_NAME);
		final Intent androidMarketplaceIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		activity.startActivity(androidMarketplaceIntent);
	}

	/**
	 * Updates the saved date in persistent storage.
	 * Used to when determining if it has been 30 days since the last optional upgrade
	 * messsage was shown.
	 */
	protected void updateDateInPrefs() {
		final SharedPreferences prefs = activity.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(DATETIME_KEY, new Date().getTime());
		editor.commit();
	}
	
}
