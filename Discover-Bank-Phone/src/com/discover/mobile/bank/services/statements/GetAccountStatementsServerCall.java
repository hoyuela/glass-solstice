package com.discover.mobile.bank.services.statements;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.bank.statements.Statement;
import com.discover.mobile.bank.statements.StatementList;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.utils.StringUtility;
/*
 * This class is used to make a server call to obtain the statement information 
 * of one account.
 */
public class GetAccountStatementsServerCall 
extends BankUnamedListJsonResponseMappingNetworkServiceCall<StatementList, Statement>{

	/*handler for this async call*/
	private final TypedReferenceHandler<StatementList> handler;
	/*String appended to the end of the url to direct call to obtain statements*/
	private final static String endUrl = "statements";
	/*Account for which statements are being retrieved*/
	private final Account account;
	
	public GetAccountStatementsServerCall(Context context,
			final AsyncCallback<StatementList> callback, final Account account) {
		
		super(context, new GetCallParams(generateUrl(account)) {
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
		}, StatementList.class, Statement.class);
		this.handler = new SimpleReferenceHandler<StatementList>(callback);
		this.account = account;
	}

	@Override
	protected TypedReferenceHandler<StatementList> getHandler() {
		return handler;
	}
	
	/*
	 * This function is used to generate the correct url for the 
	 * statements call.  Url format is as follows
	 * {baseUrl}/api/accounts/{accountID}/statements
	 */
	private static String generateUrl(final Account account){
		final StringBuilder url = new StringBuilder();
		final String baseUrl = BankUrlManager.getUrl(BankUrlManager.ACCOUNT_URL_KEY);
		url.append((baseUrl.endsWith(StringUtility.SLASH)) ? baseUrl : baseUrl + StringUtility.SLASH );
		url.append(account.id);
		url.append(StringUtility.SLASH + GetAccountStatementsServerCall.endUrl);
		return url.toString();
	}
	
	/*
	 * Successful server call.  Parse the response so that statement list will be created for this
	 * account
	 */
	@Override
	protected StatementList parseSuccessResponse(final int status, final Map<String, List<String>> headers, final InputStream body){
		StatementList statementList = new StatementList();
		try {
			statementList.statementList = super.parseUnamedList(body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//cached the response
		BankUser.instance().getAccountsToStatementsMap().put(account.id, statementList);
		return statementList;
	}

	/*Return the account these statements belong to*/
	public Account getAccount() {
		return this.account;
	}
	
}
