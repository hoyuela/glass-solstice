package com.discover.mobile.common.account.summary;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.CardUrlManager;

/**
 * Get the late payment warning information
 * @author jthornton
 *
 */
public class GetLatePaymentWarning  extends JsonResponseMappingNetworkServiceCall<LatePaymentWarningDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<LatePaymentWarningDetail> handler;

	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetLatePaymentWarning(final Context context, final AsyncCallback<LatePaymentWarningDetail> callback){
		super(context, new GetCallParams(CardUrlManager.getLatePaymentWarningUrl()) {{

			sendDeviceIdentifiers = true;
		}}, LatePaymentWarningDetail.class);

		handler = new SimpleReferenceHandler<LatePaymentWarningDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<LatePaymentWarningDetail> getHandler() {
		return handler;
	}
}