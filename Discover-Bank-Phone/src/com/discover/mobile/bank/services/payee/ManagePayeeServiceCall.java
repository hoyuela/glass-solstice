package com.discover.mobile.bank.services.payee;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;

public class ManagePayeeServiceCall extends GetPayeeServiceCall{

	/**
	 * This class exists to differentiate navigational flow between the GetPayeeService call.
	 * It performs the exact same server call as its parent class. 
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public ManagePayeeServiceCall(final Context context, final AsyncCallback<ListPayeeDetail> callback) {

		super(context,callback);
	
	}
}
