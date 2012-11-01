package com.discover.mobile.common.auth.test;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.AsyncCallback;

public class PreAuthCheckCallTest extends TestCase {

	private static final String TAG = PreAuthCheckCallTest.class.getSimpleName();
	
	@SmallTest
	public static void testCallbackExists() {
		final AsyncCallback<PreAuthResult> callback = new AsyncCallback<PreAuthCheckCall.PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				Log.e(TAG, "Status code: " + value.statusCode);
			}

			@Override
			public void error(final Object error) {
				Log.e(TAG, "Error: " + error);
			}
		};
		
		assertNotNull(callback);
	}
	
}
