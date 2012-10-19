package com.discover.mobile.common.auth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;

public class AuthService {
	
	// TODO temporary
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private static final String TAG = AuthService.class.getSimpleName();
	
	public void preAuthCheck() {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection urlConnection = null;
				URL url;
				int responseCode;
				
				try {
					url = new URL("https://www.discovercard.com/cardsvcs/acs/session/preauthcheck");
					
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setRequestProperty("X-Client-Platform", "Android");
					urlConnection.setRequestProperty("X-Application-Version", "4.00");
					
					urlConnection.setReadTimeout(15*1000);
					urlConnection.connect();
					
		            responseCode = urlConnection.getResponseCode();
		            
		            Log.d(TAG, "Response code: " + responseCode);
		        } catch (final ClientProtocolException e)  {
		        	if(urlConnection != null)
		        		urlConnection.disconnect();
		            e.printStackTrace();
		        } catch (final IOException e) {
		        	if(urlConnection != null)
		        		urlConnection.disconnect();
		            e.printStackTrace();
		        }
			}
			
		});
	}
}
