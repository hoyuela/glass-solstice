package com.discover.mobile.common.auth;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.response.AsyncCallback;

public class StrongAuthAnswerCall extends NetworkServiceCall<Object> {
	
	private static final String TAG = StrongAuthAnswerCall.class.getSimpleName();
	private static final String ID_PREFIX = "%&(()!12[";

	private final TypedReferenceHandler<Object> handler;

	public StrongAuthAnswerCall(final Context context, final AsyncCallback<Object> callback,
			final StrongAuthAnswerDetails strongAuthAnswerDetails) throws NoSuchAlgorithmException {
		
		super(context, new PostCallParams("/cardsvcs/acs/strongauth/v1/authenticate") {{
			
			clearsSessionBeforeRequest = true;

			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
			
			final TelephonyManager telephonyManager =
					(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			strongAuthAnswerDetails.did = getSha256Hash(telephonyManager.getDeviceId());
			strongAuthAnswerDetails.sid = getSha256Hash(telephonyManager.getSimSerialNumber());
			strongAuthAnswerDetails.oid = getSha256Hash(telephonyManager.getDeviceId());
			
			body = strongAuthAnswerDetails;
		}});
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<Object>(callback);
	}

	
	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}


	@Override
	protected Object parseSuccessResponse(int status,
			Map<String, List<String>> headers, InputStream body)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String getSha256Hash(final String toHash) throws NoSuchAlgorithmException {
		final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX + toHash;
		
		final MessageDigest digester = MessageDigest.getInstance("SHA-256");
		final byte[] preHash = safeToHash.getBytes(); // TODO consider specifying charset
		
		// Reset happens automatically after digester.digest() but we don't know its state beforehand so call reset()
		digester.reset();
		final byte[] postHash = digester.digest(preHash);
		
		return convertToHex(postHash);
	}
	
	private static String convertToHex(final byte[] data) {
		return String.format("%0" + data.length * 2 + 'x', new BigInteger(1, data));
	}
}
