/**
 * 
 */
package com.discover.mobile.bank.services.deposit;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.EnrollmentServiceCall;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.common.callback.AsyncCallback;

/**
 * Service class used to make a GET request to the Bank Deposit REST Service end-point in order to receive the
 * eligibility of a customer for deposit services. This class is based off of EnrollmentService call
 * whose parseSuccessResponse is called when receiving a successful response.
 * 
 * Request:
 * Payments API: GET https://asys.discoverbank.com/api/deposits/enrollment
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
 *	            "ref": ""
 *	        },
 *	        "terms": {
 *	            "allowed": [
 *	                "GET"
 *	            ],
 *	            "ref": ""
 *	        }
 *	    }
 *	}
 * 
 * @author henryoyuela
 * 
 */
public final class DepositEnrollServiceCall extends EnrollmentServiceCall {
	/**
	 * 
	 * @param context
	 *            Reference to the context invoking the API
	 * @param callback
	 *            Reference to the Handler for the response
	 */
	public DepositEnrollServiceCall(final Context context, final AsyncCallback<Eligibility> callback) {
		super(context, callback, BankUser.instance().getCustomerInfo().getEligibilityValues(BankUrlManager.DEPOSITS_URL_KEY));
	}

}
