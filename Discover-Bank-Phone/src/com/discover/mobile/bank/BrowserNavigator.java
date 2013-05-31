package com.discover.mobile.bank;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;

/**
 * Activity used to intercept intents raised with the scheme com.discover.mobile. Used for prompting a user with a modal
 * before navigating them to the browser. Any TextView that is enables its links where the links have the scheme com.discover.mobile
 * will start this activity. The activity is transparent and purpose is only to intercept the intent and display a 
 * "You are leaving this application" modal.
 * 
 * @author henryoyuela
 * 
 */
public final class BrowserNavigator extends BaseActivity {
	private static final String TAG = BrowserNavigator.class.getSimpleName();

	@Override
	public void onResume() {
		super.onResume();

		navigateToBrowser(getIntent().getData());
	}


	/**
	 * Method used to navigate to the browser via the url stored in the data
	 * object. The data object is expected to have a string with the scheme
	 * com.discover.mobile://. This scheme is replaced with https and the user
	 * is prompted with a modal that they will be leaving the application.
	 * 
	 * @param data
	 *            Holds the URL used to open the device default browser.
	 * 
	 * @return Runnable that will open
	 */
	public void navigateToBrowser(final Uri data) {

		if (data != null) {

			final String url = data.toString().replace("com.discover.mobile", "https");

			ModalAlertWithOneButton modal = null;

			// Create a one button modal to notify the user that they are
			// leaving the application
			modal = new ModalAlertWithOneButton(this, R.string.bank_open_browser_title, R.string.bank_open_browser_text, R.string.continue_text);

			/** Needs to be final in order to dismiss in listener */
			final ModalAlertWithOneButton modalParam = modal;
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
			((ModalDefaultTopView) modal.getTop()).hideNeedHelpFooter();

			showCustomAlert(modal);
		}
	}

	@Override
	public ErrorHandler getErrorHandler() {
	
		return BankErrorHandler.getInstance();
	}

}