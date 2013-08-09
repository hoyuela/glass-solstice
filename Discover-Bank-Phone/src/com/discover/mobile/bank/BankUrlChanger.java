package com.discover.mobile.bank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.google.common.base.Strings;

/**
 * BroadCastReceiver used to listen for the com.discover.mobile.CHANGE_URL_BROADCAST_INTENT intent to change the
 * BASE URL used for accessing BANK Service API.
 * 
 * @author henryoyuela
 *
 */
public final class BankUrlChanger extends BroadcastReceiver {
	private static final String TAG = BankUrlChanger.class.getSimpleName();

	@Override
	public void onReceive(final Context arg0, final Intent arg1) {

		//Check to see if the app has been configured for url changing. 
		if ("true".equalsIgnoreCase(arg0.getString(R.string.bank_url_changer_enabled))) {
			/**Key used to read new base url from Bundle passed via INTENT*/
			final String NEW_BASE_URL = "NEW_BASE_URL";

			/**Read new base url from bundle*/
			final String newUrl = arg1.getStringExtra(NEW_BASE_URL);

			if( !Strings.isNullOrEmpty(newUrl) ) {
				// Create an Intent to launch ExampleActivity
				final Intent intent = new Intent(arg0, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent);
				BankUrlManager.setBaseUrl(newUrl);
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.v(TAG, "Unable to update BASE URL, invalid value!");
				}
			}
		}
	}
}
