package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.TransferDeletionType;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.XHttpMethodOverrideValues;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.collect.ImmutableMap;

public class DeleteTransferServiceCall extends BankNetworkServiceCall<ActivityDetail>{

	// Resources containing custom timeout values.
	private static final int CONNECT_TIMEOUT_RES = R.string.timeout_connect_transfers_delete;
	private static final int READ_TIMEOUT_RES = R.string.timeout_read_transfers_delete;
	
	private final ActivityDetail activityDetail;
	private final TypedReferenceHandler<ActivityDetail> handler;
	private final TransferDeletionType deletionType;
	private final boolean isFromAccountActivity;

	/**
	 * Performs a deletion call to the server in order to delete a given scheduled transfer.  The service
	 * call can delete One Time transfers as well as support deleting both the next transfer and the entire
	 * series of transfers.
	 * 
	 * @param context			- Context of the service call
	 * @param callback			- Place to perform the call back method too.
	 * @param activityDetail	- Activity to perform the deletion on
	 * @param deletionType		- Type of deletion (e.g. One Time, Next Transfer or Entire Series)
	 */
	public DeleteTransferServiceCall(final Context context, final AsyncCallback<ActivityDetail> callback, 
									final ActivityDetail activityDetail, final TransferDeletionType deletionType, 
									final boolean isFromAccountActivity) {
		super(context, new PostCallParams(generateUrl(activityDetail, getDeleteType(deletionType))) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
				
				this.connectTimeoutSeconds = ServiceCallParams.parseTimeout(context, CONNECT_TIMEOUT_RES);
				this.readTimeoutSeconds = ServiceCallParams.parseTimeout(context, READ_TIMEOUT_RES);

				//Custom headers for delete
				headers = ImmutableMap.<String,String>builder()
						.put(HttpHeaders.XHttpMethodOveride, XHttpMethodOverrideValues.DELETE.toString())
						.build();

			}
		});
		this.deletionType = deletionType;
		this.isFromAccountActivity = isFromAccountActivity;

		//Hold a reference to payment details for providing context to call backs
		this.activityDetail = activityDetail;

		// TODO decide if this is the best type of handler
		handler = new SimpleReferenceHandler<ActivityDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<ActivityDetail> getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}

	@Override
	protected ActivityDetail parseSuccessResponse(final int status, 
													final Map<String, List<String>> headers, final InputStream body)
			throws IOException {
		
		BankUser.instance().clearReviewTransfersCache();
		
		return null;
	}

	/**
	 * Returns a reference to the Deleted Scheduled Transfer.
	 * 
	 * @return		- Deleted Scheduled Transfer
	 */
	public ActivityDetail getActivityDetail() {
		return activityDetail;
	}

	private static String generateUrl(final ActivityDetail activityDetail, final String deletionType) {
		final StringBuilder url = new StringBuilder();		
		final String baseUrl = BankUrlManager.getUrl(BankUrlManager.TRANSFER_URL_KEY);

		url.append((baseUrl.endsWith(StringUtility.SLASH) ? baseUrl : baseUrl + StringUtility.SLASH));
		url.append(activityDetail.id);
		url.append(deletionType);
		return url.toString();
	}
	
	public TransferDeletionType getDeletionType() {
		return deletionType;
	}

	private static String getDeleteType(final TransferDeletionType recurringDeletionType) {
		String deleteType = StringUtility.EMPTY;

		switch (recurringDeletionType) {
		case DELETE_ALL_TRANSFERS:
			deleteType = BankUrlManager.CANCEL_ALL_REMAINING_TRANSERS;
			break;
		case DELETE_NEXT_TRANSFER:
			deleteType = BankUrlManager.CANCEL_NEXT_TRANSFER;
			break;
		case DELETE_ONE_TIME_TRANSFER:
			deleteType = BankUrlManager.CANCEL_SCHEDULED_TRANSFER;
			break;
		}

		return deleteType;
	}

	
	public boolean isFromAccountActivity() {
		return isFromAccountActivity;
	}
}
