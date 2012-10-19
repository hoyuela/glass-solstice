package com.discover.mobile.common.auth;

import java.io.InputStream;

import android.content.Context;
import android.os.Handler;

import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.AsyncCallback;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.WeakReferenceHandler;

public class PreAuthCheckCall extends NetworkServiceCall<PreAuthResult> {
	
	private static final String TAG = PreAuthCheckCall.class.getSimpleName();
	
	private static final ServiceCallParams STANDARD_PARAMS = new ServiceCallParams() {{
		method = HttpMethod.GET;
		path = "/cardsvcs/acs/session/preauthcheck";
	}};
	
	private final Handler handler;
	
	public PreAuthCheckCall(final Context context, final AsyncCallback<PreAuthResult> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new WeakReferenceHandler<PreAuthResult>(callback);
	}
	
	@Override
	protected Handler getHandler() {
		return handler;
	}

	@Override
	protected PreAuthResult parseResponse(final InputStream responseStream, final int resultCode) {
		// TODO
		
		// TEMP
		return new PreAuthResult() {{
			statusCode = resultCode;
		}};
	}
	
//	public void preAuthCheck() {
////		executor.submit(new Runnable() {
////
////			@Override
////			public void run() {
////				// TODO Auto-generated method stub
//				
//				HttpURLConnection urlConnection = null;
//				URL url;
//				int responseCode;
//				
//				try {
//					url = new URL("https://www.discovercard.com/cardsvcs/acs/session/preauthcheck");
//					
//					urlConnection = (HttpURLConnection) url.openConnection();
////					urlConnection.setRequestMethod("GET");
////					urlConnection.setRequestProperty("X-Client-Platform", "Android");
////					urlConnection.setRequestProperty("X-Application-Version", "4.00");
//					
////					urlConnection.setReadTimeout(15*1000);
////					urlConnection.connect();
//					
//		            responseCode = urlConnection.getResponseCode();
//		            
//		            Log.d(TAG, "Response code: " + responseCode);
//		        } catch (final ClientProtocolException e)  {
//		        	if(urlConnection != null)
//		        		urlConnection.disconnect();
//		            e.printStackTrace();
//		        } catch (final IOException e) {
//		        	if(urlConnection != null)
//		        		urlConnection.disconnect();
//		            e.printStackTrace();
//		        }
////			}
////			
////		});
//	}
	
	public class PreAuthResult {
		// TEMP
		public int statusCode;
		
		// TODO
	}
	
}
