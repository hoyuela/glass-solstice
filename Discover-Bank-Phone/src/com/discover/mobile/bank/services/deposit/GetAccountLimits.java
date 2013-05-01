package com.discover.mobile.bank.services.deposit;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Used for invoking the Bank - Get Account Limits Service API invoked via GET /api/deposits/limits/{id}. 
 * The JSON response to this web-service API is de-serialized into a AccountLimits object and passed to the
 * application layer for processing. After the limits have been downloaded for the account specified in
 * the constructor at instantiation, the limits are cached in the same account object.
 * 
 * The following is an example of the Get Account Limit Service JSON response:
 * {
 *		"index" : 2,
 *		"monthlyDepositCount": {
 *			"limit": 50,
 *			"remaining": 45,
 *			"error" : {
 *				"code" : "Deposits.Limits.MontlyCount",
 *				"message" : "We're sorry. This deposit will exceed the limit on this account of {amount} deposit items per rolling-30-days."
 *			}
 *		},
 *		"monthlyDepositAmount": {
 *			"limit": 2500000,
 *			"remaining": 2388000,
 *			"error" : {
 *				"code" : "Deposits.Limits.MonthlyAmount",
 *				"message" : "We're sorry. This deposit amount will exceed the total limit on this account of {amount} per rolling-30-days."		
 *			}
 *		},
 *		"dailyDepositCount": {
 *			"limit": 15,
 *			"remaining": 15,
 *			"error" : {
 *				"code" : "Deposits.Limits.DailyCount",
 *				"message" : "We're sorry. This deposit will exceed the limit on this account of {amount} deposit items per day."
 *			}
 *		},
 *		"dailyDepositAmount": {
 *			"limit": 500000,
 *			"remaining": 500000,
 *			"error" : {
 *				"code" : "Deposits.Limits.DailyAmount",
 *				"message" : "We're sorry. This deposit amount will exceed the total limit on this account of {amount} per day."
 *			}
 *		},
 *		"depositAmount": {
 *			"limit": 500000,
 *			"remaining": 500000,
 *			"error" : {
 *				"code" : "Deposits.Limits.SingleAmount",
 *				"message" : "We're sorry. This deposit amount exceeds the limit on this account of {amount} per deposit."
 *			}
 *		},
 *		"links" : {
 *			"self" : {
 *				"ref" : "https://www.discoverbank.com/api/deposits/limits/2",
 *				"allowed" : [ "GET" ]
 *			}
 *		}
 *	} 
 * 
 * @author henryoyuela
 *
 */
public class GetAccountLimits  extends BankJsonResponseMappingNetworkServiceCall<AccountLimits>{

	/**
	 * Used for printing logs into Android Logcat
	 */
	private static final String TAG = GetAccountLimits.class.getSimpleName();

	/** Reference handler to return the data to the UI */
	private final TypedReferenceHandler<AccountLimits> handler;

	/** Reference to the Account with the information that will be sent to the server for fetching limits**/
	private final Account accountRef;
	
	public GetAccountLimits(final Context context, final AsyncCallback<AccountLimits> callback, final Account account) {		
		super(context, generateCallParams(account), AccountLimits.class);
		
		this.accountRef = account;
		
		handler = new StrongReferenceHandler<AccountLimits>(callback);
	}

	/**
	 * Method used to generate the ServiceCallParams used to configure the handling of a Service Call when
	 * a request is sent; in addition to when a response is received.
	 * @param details 
	 * 
	 * @return Returns a PostCallParams that is to be provided to the NetworkServiceCall<> base class in the constructor.
	 */
	private static GetCallParams generateCallParams(final Account account) {
		//TODO: Needs to get updated once server side provides the url even if user is not enrolled
		final String url = "/api/deposits/limits/" +account.id;
		
		final GetCallParams callParams = new GetCallParams(url);
		// This service call is made after authenticating and receiving
		// a token, therefore the session should not be cleared otherwise the
		// token will be wiped out
		callParams.clearsSessionBeforeRequest = false;

		// This ensures the token is added to the HTTP Authorization
		// Header of the HTTP request
		callParams.requiresSessionForRequest = true;

		// This ensure the required device information is supplied in
		// the Headers of the HTTP request
		callParams.sendDeviceIdentifiers = true;
		
		// Specify what error parser to use when receiving an error
		// response is received
		callParams.errorResponseParser = BankErrorResponseParser.instance();
				
		return callParams;

	}
	
	@Override
	protected TypedReferenceHandler<AccountLimits> getHandler() {
		return handler;
	}
	
	@Override
	protected AccountLimits parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final AccountLimits data = super.parseSuccessResponse(status, headers, body);
		
		//Cache limits for an account
		BankUser.instance().getAccount(accountRef.id).limits = data;
		
		return data;
	}

	/**
	 * 
	 * @return Returns reference to account whose limits were requested.
	 */
	public Account getAccount() {
		return accountRef;
	}
}
