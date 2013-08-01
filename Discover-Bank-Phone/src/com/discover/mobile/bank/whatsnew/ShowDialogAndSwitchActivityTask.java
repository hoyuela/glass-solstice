package com.discover.mobile.bank.whatsnew;

import com.discover.mobile.bank.framework.BankConductor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/*
 * This class is used to show a progress dialog and move the current activity
 * to the home page.  This is used so that the spinner in the dialog 
 * will spin.  Setting the dialog from whatsnewactivity would not allow the 
 * spinner to spin
 */
public class ShowDialogAndSwitchActivityTask extends AsyncTask<Void, Void, Void> {
	/**context of the application that is about to be destroyed*/
	private Context context;
	/**progress dialog to display to the user*/
	private ProgressDialog progress;
	
	
	public ShowDialogAndSwitchActivityTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(context, "Disover", "Loading...", true);
	}
	
	@Override
	protected void onPostExecute(Void result){
		progress.dismiss();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		//navigate to the home page
		BankConductor.navigateToHomePage();
		return null;
	}

}
