package com.discover.mobile.bank.services.deposit;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * Service call to submit a check deposit.
 * Sends a DepositDetail to the server through a POST request and then receives a DepositDetail
 * back from the server with a 201 Created response if it was successful.
 * 
 * @author scottseward
 *
 */
public class SubmitCheckDepositCall extends BankJsonResponseMappingNetworkServiceCall<DepositDetail> {

		/**Reference handler to allow the call to be back on the UI*/
		private final SimpleReferenceHandler<DepositDetail> handler;
		/**Holds the result from the response from the server*/
		private DepositDetail result;
		/**Flag set to true if the response to this service call has been handled or not*/
		private boolean handled;
		
		public SubmitCheckDepositCall(final Context context, final AsyncCallback<DepositDetail> callback,
				final DepositDetail modelClass) {
			super(context, new PostCallParams("/api/deposits") {{
				requiresSessionForRequest = true;
				sendDeviceIdentifiers = true;

				body = modelClass;
				
				errorResponseParser = BankErrorResponseParser.instance();
			}},
			DepositDetail.class);
			handler = new SimpleReferenceHandler<DepositDetail>(callback);
		}

		@Override
		protected TypedReferenceHandler<DepositDetail> getHandler() {
			// TODO Auto-generated method stub
			
			
			return handler;
		}
		
		@Override
		protected DepositDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
				throws IOException {
			result = super.parseSuccessResponse(status, headers, body);	
			
			return result;
		}
		
		/**
		 * @return Returns the result from the response from the server.
		 */
		public DepositDetail getResult() {
			return result;
		}
		
		/**
		 * Method used to set whether this service call has been handled or not.
		 * 
		 * @param value True if handled, false otherwise.
		 */
		public void setHandled(final boolean value) {
			handled = value;
		}
		
		/**
		 * 
		 * @return True if the response to this network service call has been handled, false otherwise
		 */
		public boolean isHandled() {
			return handled;
		}

	}