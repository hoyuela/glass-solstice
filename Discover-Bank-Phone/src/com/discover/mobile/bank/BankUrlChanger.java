package com.discover.mobile.bank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.services.BankUrlManager;
import com.google.common.base.Strings;

/**
 * BroadCastReceiver used to listen for the com.discover.mobile.CHANGE_URL_BROADCAST_INTENT intent to change the
 * BASE URL used for accessing BANK Service API.
 * 
 * @author henryoyuela
 *
 */
final public class BankUrlChanger extends BroadcastReceiver {
	private static final String TAG = BankUrlChanger.class.getSimpleName();

	@Override
	public void onReceive(final Context arg0, final Intent arg1) {
		/**Key used to read new base url from Bundle passed via INTENT*/
		final String NEW_BASE_URL = "NEW_BASE_URL";
		/**Key used to return result to activity that sent Bundle with new base url via an INTENT*/
		final String CHANGE_ACK	= "CHANGE_ACK";
		
		/**Read bundle from intent*/
		final Bundle extras = getResultExtras(true);

		/**Read new base url from bundle*/
		final String newUrl = arg1.getStringExtra(NEW_BASE_URL);
		
		if( !Strings.isNullOrEmpty(newUrl) ) {
			BankUrlManager.setBaseUrl(newUrl);
			extras.putBoolean(CHANGE_ACK, true);
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.v(TAG, "Unable to update BASE URL, invalid value!");
			}
			extras.putBoolean(CHANGE_ACK, false);
		}

		
		setResultExtras(extras);
		
	}

}
