package com.discover.mobile.bank;

import android.net.Uri;
import android.os.Handler;

import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;

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
	public static String METHOD_SCHEME = "method";

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
			} else {
				startActivity(getIntent());
			}
		}
	}

	@Override
	public ErrorHandler getErrorHandler() {

		return BankErrorHandler.getInstance();
	}

}