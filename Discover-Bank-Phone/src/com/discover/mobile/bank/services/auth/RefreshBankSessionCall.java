package com.discover.mobile.bank.services.auth;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class RefreshBankSessionCall extends BankJsonResponseMappingNetworkServiceCall<Object> {

	protected RefreshBankSessionCall(Context context, ServiceCallParams params,
			Class<BankLoginData> modelClass) {
		super(context, params, null);
		

		
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
