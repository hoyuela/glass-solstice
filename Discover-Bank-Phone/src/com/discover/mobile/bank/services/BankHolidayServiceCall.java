package com.discover.mobile.bank.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * Class used to download a list of dates that are considered Bank Holidays via the Bank Web Service API.
 * 
 * @author henryoyuela
 *
 */
public class BankHolidayServiceCall extends BankUnamedListJsonResponseMappingNetworkServiceCall<BankHolidays, String>{

	private final TypedReferenceHandler<BankHolidays> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public BankHolidayServiceCall(final Context context, final AsyncCallback<BankHolidays> callback) {

		super(context, new GetCallParams(BankUrlManager.getBankHolidaysUrl()) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, BankHolidays.class, String.class);

		handler = new StrongReferenceHandler<BankHolidays>(callback);
	}
	
	@Override
	protected TypedReferenceHandler<BankHolidays> getHandler() {
		return handler;
	}

	@Override
	protected BankHolidays parseSuccessResponse(final int status, final Map headers,
			final InputStream body) throws IOException {		
		final BankHolidays data = new BankHolidays();
		
		data.holidays = super.parseUnamedList(body);
		
		/**Cache result in BankUser*/
		if( data != null ) {
			BankUser.instance().setHolidays(data);
		}
		return data;
	}
}
