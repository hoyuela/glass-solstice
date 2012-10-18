package com.discover.mobile.common.auth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class AuthService {
	
	// TODO temporary
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private static final String TAG = "LoginLogout";
	
	public void preAuthCheckOld() {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String url = "https://www.discovercard.com/cardsvcs/acs/session/preauthcheck";
				int responseCode;
				
				HttpGet request = new HttpGet(url);
				request.addHeader("X-Client-Platform", "Android");
				request.addHeader("X-Application-Version", "4.00");
				
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				
				try {
		            httpResponse = client.execute(request);
		            responseCode = httpResponse.getStatusLine().getStatusCode();
		            
		            Log.d(TAG, "Response code: " + responseCode);
		        } catch (ClientProtocolException e)  {
		            client.getConnectionManager().shutdown();
		            e.printStackTrace();
		        } catch (IOException e) {
		            client.getConnectionManager().shutdown();
		            e.printStackTrace();
		        }
			}
			
		});
	}
	
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
		        } catch (ClientProtocolException e)  {
		            urlConnection.disconnect();
		            e.printStackTrace();
		        } catch (IOException e) {
		            urlConnection.disconnect();
		            e.printStackTrace();
		        }
			}
			
		});
	}
}
