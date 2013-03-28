package com.discover.mobile.card.common;

import android.content.Context;
import android.os.AsyncTask;

import com.discover.mobile.card.common.net.utility.NetworkUtility;

public class ResetServerTimeOut extends AsyncTask<String, Integer, Boolean> {
	private Context mContext;
	@Override
	protected Boolean doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		
		super.onPostExecute(result);
		
		//TODO dismiss the progress dialog
		/*
		 * Do what was there in suceess method of LoginServiceFacadeImpl
		 * 
		 */

	}
	
	private boolean checkNetworkConnected() {
		boolean isNetworkConnected=false;
		if (!NetworkUtility.isConnected(mContext)) {
			
			isNetworkConnected= false;
		}
		else
		{
			isNetworkConnected=true;
		}
		return isNetworkConnected;
		
	
	}
}