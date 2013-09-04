package com.discover.mobile.bank.services.smc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.smc.MessageList;
import com.discover.mobile.smc.MessageListItem;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;


/**
 * Service call used to retrieve a list of messages from the server.
 * To use, pass it a context, callback and a string representing the mailbox 
 * (either inbox or sentbox)
 */
public class GetMessageListServerCall 
extends BankUnamedListJsonResponseMappingNetworkServiceCall<MessageList, MessageListItem>{
	
	/**handler for the service call*/
	private final TypedReferenceHandler<MessageList> handler;

	/**End of the url to append to the base url*/
	private static final String URL_END = "messages?view=";
	/**Tag used for logging information*/
	private static final String TAG = "GetMessageListServerCall";
	
	private String mailboxType;
	
	public GetMessageListServerCall(Context context, final AsyncCallback<MessageList> callback, final String mailbox){
		super(context, new GetCallParams(generateUrl(mailbox)) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				this.clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP 
				//Authorization Header of the HTTP request
				this.requiresSessionForRequest = true;

				//This ensure the required device 
				//information is supplied in the Headers of the HTTP request
				this.sendDeviceIdentifiers = true;
				
				//Sets the service call to be cancellable
				this.setCancellable(true);

				// Specify what error parser to use when receiving an error response is received
				this.errorResponseParser = BankErrorResponseParser.instance();
			}
		}, MessageList.class, MessageListItem.class);
		
		handler = new SimpleReferenceHandler<MessageList>(callback);
		this.mailboxType = mailbox;
	}
	
	/**
	 * Generate the url for requesting the messages.
	 * @param mailbox - either inbox or sentbox
	 * @return - returns url and params to retreive messages 
	 * of selected box
	 */
	private static String generateUrl(final String mailbox) {
		return BankUrlManager.getApiUrl() + URL_END +mailbox;
	}

	/**return which mailbox was queried*/
	public String getMailBoxType() {
		return mailboxType;
	}
	
	@Override
	protected TypedReferenceHandler<MessageList> getHandler() {
		return handler;
	}
	/**
	 * successfully exectuted service call, parse the body into a list of object.
	 */
	@Override
	protected MessageList parseSuccessResponse(final int status, final Map<String, List<String>> headers, final InputStream body){
		MessageList messageList = new MessageList();
		try {
			messageList.messages = super.parseUnamedList(body);
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
		return messageList;
	}
}
