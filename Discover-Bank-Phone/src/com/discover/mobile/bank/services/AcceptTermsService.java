package com.discover.mobile.bank.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;


/**
 * The POST call made to Bank APIs to accept a terms and service agreement.
 * It does not need to map a response because the response is expected to be empty.
 * 
 * @author scottseward
 *
 */
public class AcceptTermsService extends BankNetworkServiceCall<Object> implements Serializable {
	private static final long serialVersionUID = 5311990873372013208L;

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<Object> handler;

	/**
	 * Holds Reference to Eligibility object used to construct the URL for sending a request to accept terms and conditions.
	 */
	private final Eligibility eligibility;

	/**
	 * 
	 * @param context Reference to service or activity making the service call
	 * @param callback Reference to an AsyncCallback object where callback's will be called from to notify application on status of the service call.
	 * @param eligibility Reference to an Eligibility object from where the URL for this service call will be fetched from.
	 */
	public AcceptTermsService(final Context context, final AsyncCallback<Object> callback, final Eligibility eligibility) {

		super(context, new PostCallParams(eligibility.getEnrollmentUrl()) {{
			requiresSessionForRequest = true;

			errorResponseParser = BankErrorResponseParser.instance();

		}});

		this.eligibility = eligibility;

		handler = new StrongReferenceHandler<Object>(callback);
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}

	@Override
	protected Object parseSuccessResponse(final int status, final Map<String,List<String>> headers, 
			final InputStream body)throws IOException {

		eligibility.eligible = true;
		eligibility.enrolled = true;

		return null;
	}

	/**
	 * 
	 * @return Returns reference to Eligibility object used to construct URL for accepting terms and conditions.
	 */
	public Eligibility getEligibility() {
		return this.eligibility;
	}
}