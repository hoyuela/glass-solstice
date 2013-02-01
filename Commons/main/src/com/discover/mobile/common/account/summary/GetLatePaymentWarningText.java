package com.discover.mobile.common.account.summary;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.CardUrlManager;

/**
 * Gets the text (only text) to be shown in the late payment warning modal
 * @author jthornton
 *
 */
public class GetLatePaymentWarningText extends JsonResponseMappingNetworkServiceCall<LatePaymentWarningTextDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<LatePaymentWarningTextDetail> handler;

	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetLatePaymentWarningText(final Context context, final AsyncCallback<LatePaymentWarningTextDetail> callback){
		super(context, new GetCallParams(CardUrlManager.getLatePaymentWarningTextUrl()) {{

			sendDeviceIdentifiers = true;
		}}, LatePaymentWarningTextDetail.class);

		handler = new SimpleReferenceHandler<LatePaymentWarningTextDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<LatePaymentWarningTextDetail> getHandler() {
		return handler;
	}
}