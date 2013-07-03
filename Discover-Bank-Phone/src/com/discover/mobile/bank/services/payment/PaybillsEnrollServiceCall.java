package com.discover.mobile.bank.services.payment;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.EnrollmentServiceCall;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.common.callback.AsyncCallback;

/**
 * Service class used to make a GET request to the Bank Paybills REST Service end-point in order to receive the
 * eligibility of a customer for manage payees and paybills services. This class is based off of EnrollmentService call
 * whose parseSuccessResponse is called when receiving a successful response.
 * 
 * Request:
 * Payments API: GET https://asys.discoverbank.com/api/payments/enrollment
 *
 * Response:
 *	{
 *	    "enrolled": true,
 *	    "links": {
 *	        *"enrollment": {
 *	            *"allowed": [
 *	                *"GET",
 *	                "POST"
 *	            ],
 *	            "ref": "Payments.Enrollment"
 *	        },
 *	        "terms": {
 *	            "allowed": [
 *	                "GET"
 *	            ],
 *	            "ref": "Payments.Terms"
 *	        }
 *	    }
 *	}
 * 
 * @author henryoyuela
 * 
 */
public final class PaybillsEnrollServiceCall extends EnrollmentServiceCall {
	/**
	 * 
	 * @param context
	 *            Reference to the context invoking the API
	 * @param callback
	 *            Reference to the Handler for the response
	 */
	public PaybillsEnrollServiceCall(final Context context, final AsyncCallback<Eligibility> callback) {
		super(context, callback, BankUser.instance().getCustomerInfo().getEligibilityValues(BankUrlManager.PAYMENTS_URL_KEY));
	}

}
