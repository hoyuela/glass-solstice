package com.discover.mobile.common.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.discover.mobile.common.auth.UpdateSessionCall.UpdateSessionResult;
import com.discover.mobile.common.net.AsyncCallback;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.StrongReferenceHandler;

public class UpdateSessionCall extends NetworkServiceCall<UpdateSessionResult> {
	
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	private final Handler handler;
	
	public UpdateSessionCall(final Context context, final AsyncCallback<UpdateSessionResult> callback) {
		super(context, new ServiceCallParams() {{
			method = HttpMethod.POST;
			path = "/cardsvcs/acs/session/v1/update";
		}});
		
		handler = new StrongReferenceHandler<UpdateSessionResult>(callback);
	}

	@Override
	protected Handler getHandler() {
		return handler;
	}

	@Override
	protected UpdateSessionResult parseResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body) {
		// TODO Auto-generated method stub
		return new UpdateSessionResult() {{
			statusCode = status;
		}};
	}
	
	// TODO
	public class UpdateSessionResult {
		// TEMP
		public int statusCode;
		
		// TODO
	}
}
