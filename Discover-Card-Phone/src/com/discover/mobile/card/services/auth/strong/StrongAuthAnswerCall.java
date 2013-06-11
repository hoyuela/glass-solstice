package com.discover.mobile.card.services.auth.strong;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.discover.mobile.card.services.CardNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class StrongAuthAnswerCall extends CardNetworkServiceCall<StrongAuthAnswerDetails> {
	
	
	private static final String ID_PREFIX = "%&(()!12[";

	private final TypedReferenceHandler<StrongAuthAnswerDetails> handler;

	public StrongAuthAnswerCall(final Context context, final AsyncCallback<StrongAuthAnswerDetails> callback,
			final StrongAuthAnswerDetails strongAuthAnswerDetails) throws NoSuchAlgorithmException {
		
		super(context, new PostCallParams(CardUrlManager.getStrongAuthAnswerUrl()) {{
			
			requiresSessionForRequest = true;
			
			sendDeviceIdentifiers = true;
			
			// TODO combine with the code for this in NetworkServiceCall
			final TelephonyManager telephonyManager =
					(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			strongAuthAnswerDetails.did = getSha256Hash(telephonyManager.getDeviceId());
			strongAuthAnswerDetails.sid = getSha256Hash(telephonyManager.getSimSerialNumber());
			strongAuthAnswerDetails.oid = getSha256Hash(telephonyManager.getDeviceId());
			
			body = strongAuthAnswerDetails;
		}});
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<StrongAuthAnswerDetails>(callback);
	}

	
	@Override
	protected TypedReferenceHandler<StrongAuthAnswerDetails> getHandler() {
		return handler;
	}


	@Override
	protected StrongAuthAnswerDetails parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body)
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
