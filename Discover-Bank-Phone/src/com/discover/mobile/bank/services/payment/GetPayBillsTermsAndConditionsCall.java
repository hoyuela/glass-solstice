package com.discover.mobile.bank.services.payment;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Get the pay bills terms and conditions conte that will be displayed to the user for
 * review before they decide to accept or decline it.
 * 
 * It is a GET request to the Bank APIs.
 * @author scottseward
 *
 */
public class GetPayBillsTermsAndConditionsCall extends 
										BankJsonResponseMappingNetworkServiceCall<PayBillsTermsAndConditionsDetail>{
	private final TypedReferenceHandler<PayBillsTermsAndConditionsDetail> handler;

	public GetPayBillsTermsAndConditionsCall(final Context context,
			final AsyncCallback<PayBillsTermsAndConditionsDetail> callback) {
		super(context, 
				new GetCallParams(BankUrlManager.getTermsAndConditionsUrl()){{
			// TODO empty get call params. No Get call params were needed at the time of writing this
		}}, PayBillsTermsAndConditionsDetail.class);
		handler = new StrongReferenceHandler<PayBillsTermsAndConditionsDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PayBillsTermsAndConditionsDetail> getHandler() {
		return handler;
	}

}
