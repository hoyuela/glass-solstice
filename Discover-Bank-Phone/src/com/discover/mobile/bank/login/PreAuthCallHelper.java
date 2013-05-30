package com.discover.mobile.bank.login;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.modals.BankModalAlertWithTwoButtons;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;


/**
 * PreAuthCallHelper
 * 
 * This is the helper class of the success and error response parsers for preauth.
 * This class handles the reading and writing of the dates to the preferences file so that we can check
 * when an optional update is required (30 days since last seen)
 * 
 * @author scottseward, ekaram
 *
 */
public final class PreAuthCallHelper  {


	protected static final String DATETIME_KEY = "com.discover.mobile.optionalupdatedate";

	protected static final int NO_DATE = 0;
	protected static final int OPTIONAL_UPGRADE = 1;
	protected static final int FORCED_UPGRADE = 2;
	protected static final int DOES_NOT_EXIST = 0;

	protected static final long DAY_IN_MILLISECONDS = 86400000;
	protected static final int OPTIONAL_UPGRADE_DAYS = 30;
	protected static final String PACKAGE_NAME = "com.discoverfinancial.mobile";

	protected static final String PREFS_FILE = "UpdatePreferences";

	private PreAuthCallHelper() {
		// No need to instantiate or extend this class since all methods are static.
		throw new AssertionError();
	}

	/**
	 * Shows the optional upgrade message to the user.
	 * This message is cancelable with no consequences. The user also has the option to press
	 * upgrade, which will take them to the Google Play store and the Discover app page.
	 * 
	 * @param message The message to be presented in the alert dialog.
	 * 
	 */
	public static final void showOptionalUpgradeAlertDialog(final ErrorHandlerUi errorHandlerUi, final String message) {
		final Context context = errorHandlerUi.getContext();

		final ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(context, null);
		final ModalDefaultTwoButtonBottomView twoButtonBottomView = new ModalDefaultTwoButtonBottomView(context, null);

		titleAndContentForDialog.hideNeedHelpFooter();
		titleAndContentForDialog.setTitle(R.string.option_upgrade_dialog_title);
		titleAndContentForDialog.setContent(R.string.optional_upgrade_dialog_body);

		twoButtonBottomView.setOkButtonText(R.string.upgrade_dialog_button_text);
		twoButtonBottomView.setCancelButtonText(R.string.no_thanks);

		final BankModalAlertWithTwoButtons optionalUpgradeDialog = 
				new BankModalAlertWithTwoButtons(context, titleAndContentForDialog, twoButtonBottomView);

		twoButtonBottomView.getOkButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) { upgrade(context); }
		});

		twoButtonBottomView.getCancelButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) { optionalUpgradeDialog.dismiss(); }
		});
		errorHandlerUi.showCustomAlert(optionalUpgradeDialog);
	}

	/**
	 * showForcedUpgradeAlertDialog()
	 * This method, when called, shows an alert dialog with the forced upgrade text.
	 * This dialog needs to prevent the user from using the application.
	 * If the user chooses upgrade, they are directed to the Google Play store page for the Discover application.
	 * If the user cancels the dialog by pressing the back button or otherwise, the application is force quit.
	 */
	public static  final void showForcedUpgradeAlertDialog(final ErrorHandlerUi errorHandlerUi) {
		final Context context = errorHandlerUi.getContext();
		final ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(context, null);
		final ModalDefaultOneButtonBottomView singleButtonBottomView = new ModalDefaultOneButtonBottomView(context, null);

		titleAndContentForDialog.hideNeedHelpFooter();
		titleAndContentForDialog.setTitle(R.string.forced_upgrade_dialog_title);
		titleAndContentForDialog.setContent(R.string.forced_upgrade_dialog_body);
		titleAndContentForDialog.showErrorIcon(true);

		singleButtonBottomView.setButtonText(R.string.upgrade_dialog_button_text);

		final ModalAlertWithOneButton optionalUpgradeDialog = 
				new ModalAlertWithOneButton(context, titleAndContentForDialog, singleButtonBottomView);

		singleButtonBottomView.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) { upgrade(context); }
		});
		optionalUpgradeDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(final DialogInterface dialog) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});	

		if(!DiscoverModalManager.hasActiveModal()){
			errorHandlerUi.showCustomAlert(optionalUpgradeDialog);
		}
	}

	/**
	 * shouldPresentOptionalUpdate()
	 * @param updateDescription The upgrade message to be displayed in the dialog.
	 * @return 
	 */
	protected static final boolean shouldPresentOptionalUpdate(final Activity activity, final String updateDescription) {
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
				updateDateInPrefs(activity);
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
	protected static void upgrade(final Context context) {
		final Uri marketUri = Uri.parse("market://details?id=" + PACKAGE_NAME);
		final Intent androidMarketplaceIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		((LoginActivity)context).startActivityNoReset(androidMarketplaceIntent);
	}

	/**
	 * Updates the saved date in persistent storage.
	 * Used to when determining if it has been 30 days since the last optional upgrade
	 * messsage was shown.
	 */
	protected static void updateDateInPrefs(final Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

		final SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(DATETIME_KEY, new Date().getTime());
		editor.commit();
	}

}
