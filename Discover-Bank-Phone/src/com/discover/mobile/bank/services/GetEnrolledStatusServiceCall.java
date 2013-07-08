package com.discover.mobile.bank.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Abstract class used to make a GET request to a Bank REST Service end-point in order to receive the eligibility of a
 * customer for a specific service. This class is based off of BankJsonResponseMappingNetworkServiceCall whose
 * parseSuccessResponse is called when receiving a successful response.
 * 
 * This class will update the BankUser singleton instance with the eligibility for the service requested.
 * 
 * @author henryoyuela
 * 
 */
public abstract class GetEnrolledStatusServiceCall extends BankJsonResponseMappingNetworkServiceCall<Eligibility> {

	private final TypedReferenceHandler<Eligibility> handler;
	private final Eligibility eligibility;

	/**
	 * 
	 * @param context
	 *            Reference to the context invoking the API
	 * @param callback
	 *            Reference to the Handler for the response
	 */
	public GetEnrolledStatusServiceCall(final Context context, final AsyncCallback<Eligibility> callback, final Eligibility eligibility) {

		super(context, new GetCallParams(eligibility.getEnrollmentUrl()) {
			{
				// This service call is made after authenticating and receiving a token,
				// therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				// This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				// This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, Eligibility.class);

		this.eligibility = eligibility;

		handler = new StrongReferenceHandler<Eligibility>(callback);
	}

	@Override
	protected TypedReferenceHandler<Eligibility> getHandler() {
		return handler;
	}

	@Override
	protected Eligibility parseSuccessResponse(final int status, final Map<String, List<String>> headers, final InputStream body) throws IOException {

		final Eligibility newEligibility = super.parseSuccessResponse(status, headers, body);

		/** Update cache Eligibility in Customer Object */
		final Eligibility cachedEligibility = BankUser.instance().getCustomerInfo().getEligibilityValues(eligibility.service);
		cachedEligibility.enrolled = newEligibility.enrolled;
		cachedEligibility.updated = true;

		return cachedEligibility;
	}
}
