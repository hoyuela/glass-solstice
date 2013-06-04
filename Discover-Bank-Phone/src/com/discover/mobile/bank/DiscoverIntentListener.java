package com.discover.mobile.bank;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.ui.modals.SimpleContentModal;

/**
 * Activity used to intercept intents raised with the scheme com.discover.mobile. Used for prompting a user with a modal
 * before navigating them to the browser. Any TextView that is enables its links where the links have the scheme
 * com.discover.mobile will start this activity. The activity is transparent and purpose is only to intercept the intent
 * and display a "You are leaving this application" modal.
 * 
 * @author henryoyuela
 * 
 */
public final class DiscoverIntentListener extends BaseActivity {
	private static final String PROVIDE_FEEDBACK = "cardProvideFeedback";
	private static final String PRIVACY_STATEMENT = "navigateToMobilePrivacyStatement";
	public static final String METHOD_SCHEME = "method";
	public static final String BROWSER_SCHEME = "com.discover.mobile";

	@Override
	public void onResume() {
		super.onResume();

		navigateTo(getIntent().getData());
	}

	/**
	 * Method used to navigate to the browser via the url stored in the data object. The data object is expected to have
	 * a string with the scheme com.discover.mobile://. This scheme is replaced with https and the user is prompted with
	 * a modal that they will be leaving the application.
	 * 
	 * @param data
	 *            Holds the URL used to open the device default browser.
	 * 
	 * @return Runnable that will open
	 */
	public void navigateTo(final Uri data) {
		if (data != null) {
			// Method scheme is used to call a method defined in the application
			if (data.getScheme().equalsIgnoreCase(METHOD_SCHEME)) {
				final String method = data.getSchemeSpecificPart();
				if (method.contains(PROVIDE_FEEDBACK)) {
					BankConductor.navigateToFeedback(true);
				} else if (method.contains(PRIVACY_STATEMENT)) {
					this.finish();

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							BankConductor.navigateToCardMobilePrivacy();
						}
					}, 500);
				}
			}
			// Browser scheme is used to prompt the user with a modal before navigating
			// the user to the default browser
			else if (data.getScheme().equalsIgnoreCase(BROWSER_SCHEME)) {
				navigateToBrowser(data);
			}
			// Catch-all for all other intents
			else {
				startActivity(getIntent());
			}
		}
	}

	/**
	 * Method used to navigate to the browser via the url stored in the data object. The data object is expected to have
	 * a string with the scheme com.discover.mobile://. This scheme is replaced with https and the user is prompted with
	 * a modal that they will be leaving the application.
	 * 
	 * @param data
	 *            Holds the URL used to open the device default browser.
	 * 
	 * @return Runnable that will open
	 */
	public void navigateToBrowser(final Uri data) {

		if (data != null) {

			final String url = data.toString().replace("com.discover.mobile", "https");

			SimpleContentModal modal = null;

			// Create a one button modal to notify the user that they are
			// leaving the application
			modal = new SimpleContentModal(this, R.string.bank_open_browser_title, R.string.bank_open_browser_text, R.string.continue_text);

			/** Needs to be final in order to dismiss in listener */
			final SimpleContentModal modalParam = modal;
			// Set the dismiss listener that will navigate the user to the
			// browser
			modal.getBottom().getButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					modalParam.dismiss();
					final Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					DiscoverActivityManager.getActiveActivity().startActivity(i);

				}
			});

			modal.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(final DialogInterface arg0) {
					finish();
				}
			});

			/** Hide Need Help footer */
			modal.hideNeedHelpFooter();

			showCustomAlert(modal);
		}
	}

	@Override
	public ErrorHandler getErrorHandler() {

		return BankErrorHandler.getInstance();
	}

}