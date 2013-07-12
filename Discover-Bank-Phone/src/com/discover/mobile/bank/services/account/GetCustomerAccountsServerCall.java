package com.discover.mobile.bank.services.account;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * This is used downloading the Accounts information for a Bank user that is provided in a JSON response to a 
 * Bank web-service API invocation. 
 * 
 * The GetCustomerAccountsServerCall class sends a GET request to the Bank API Accounts Service located
 * at the URL /api/accounts. It de-serializes the JSON object, provided in a 200 OK response, into a
 * Account object. The following is an example of the JSON:
 * 
 * {
 *   "ending": "1111",
 *   "id": 1,
 *   "name": "Discover Cashback Checking",
 *   "nickname": "My Rewards Checking",
 *   "type": "CHECKING",
 *   "balance": 123456,
 *   "interestRate": {
 *         "numerator": 6,
 *         "denominator":  100,
 *         "formatted" : "0.06%"
 *   }
 *   "interestEarnedLastStatement": 123,
 *   "interestYearToDate": 4321,
 *   "openDate": 2007-04-06T16:14:24.134455Z
 *   "status" : "OPEN"
 *   "links": {
 *       "self": {
 *           "ref": "https://www.discoverbank.com/api/accounts/1",
 *            "allowed": [ "GET", "POST" ] 
 *       }
 *
 *        "postedActivity": {
 *            "ref": "https://www.discoverbank.com/api/accounts/1/activity?status=posted",
 *            "allowed": [ "GET" ]
 *        },
 *        "scheduledActivity": {
 *            "ref": "https://www.discoverbank.com/api/accounts/1/activity?status=scheduled",
 *            "allowed": [ "GET" ]
 *        }
 *
 *    }
 *}
 * @author henryoyuela
 *
 */
public class GetCustomerAccountsServerCall  extends BankUnamedListJsonResponseMappingNetworkServiceCall<AccountList, Account> {

	/**Reference handler to return the data to the UI*/
	private final TypedReferenceHandler<AccountList> handler;
	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetCustomerAccountsServerCall(final Context context, final AsyncCallback<AccountList> callback) {

		super(context, new GetCallParams(BankUrlManager.getUrl(BankUrlManager.ACCOUNT_URL_KEY)) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				this.clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				this.requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				this.sendDeviceIdentifiers = true;
				
				//Sets the service call to be cancellable
				this.setCancellable(true);

				// Specify what error parser to use when receiving an error response is received
				this.errorResponseParser = BankErrorResponseParser.instance();

			}
		}, AccountList.class, Account.class);

		this.handler = new SimpleReferenceHandler<AccountList>(callback);
	}

	/**
	 * Parse the success response.  Take the unnamed table and then parses it correctly returning a list of the
	 * POJO model class to the UI.
	 * 
	 * @param status - response status
	 * @param header - map of headers
	 * @param body - response body
	 * @return list of Accounts downloaded
	 */
	@Override
	protected AccountList parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {

		AccountList accountList = new AccountList();
		accountList.accounts = super.parseUnamedList(body);
		
		/*
		 * This if else statement is a fail safe.
		 * On mst0 server returns empty list of accounts some times.
		 * In that case this if statement will keep stale information
		 * instead of clearing out account information.
		 */
		if (!accountList.accounts.isEmpty()) {
			//Stores Accounts data into BankUser singleton instance to be referenced
			//later by the application layer and other classes
			BankUser.instance().setAccounts(accountList);
			// Set flag to false so that another download request is not made and cached accounts are used instead.
			BankUser.instance().setAccountOutDated(false);
		} else {
			//accountlist is empty, due to server issue
			//return the stale one that is cached
			accountList = BankUser.instance().getAccounts();
		}

		return accountList;
	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<AccountList> getHandler() {
		return this.handler;
	}
}
