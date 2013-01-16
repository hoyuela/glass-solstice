package com.discover.mobile.alert;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;

import com.discover.mobile.BaseActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

/**
 * Used to build error dialogs used throughout the application.
 * 
 * @author henryoyuela
 *
 */
public class ErrorModalFactory {
	static final private ErrorModalFactory instance = new ErrorModalFactory();
	static final String TAG = ErrorModalFactory.class.getSimpleName();
	
	/**
	 * Uses a singleton design pattern 
	 */
	private ErrorModalFactory() {
		
	}
	
	/**
	 * DismissListener which can be applied to an alert dialog to close the 
	 * application when it is dismissed.
	 * 
	 * @author henryoyuela
	 *
	 */
	private class CloseApplicationOnDismiss implements OnDismissListener {
		//Reference of the activity to be able to close out the application
		private final Activity activity;
		
		public CloseApplicationOnDismiss(BaseActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			//close application
			this.activity.finish();
			
		}
	}
	
	/**
	 * Creates an error dialog with a single button using the title and error text provided. The dialog created
	 * will close the application on dismiss, if the user is not logged in; otherwise, returns to the
	 * previous screen.
	 * 
	 * @param activity   Reference to the activity which created the dialog
	 * @param errorCode  HTTP error code that triggered the creation of the dialog
	 * @param titleText  Title to be applied at the top of the modal dialog
	 * @param errorText  Text to be applied as the content of the modal dialog
	 * 
	 * @return Returns the modal dialog created
	 */
	public static ModalAlertWithOneButton createErrorModal(BaseActivity activity, int errorCode, int titleText, int errorText) {
		//Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
			
		//Decide on what help number to show
		int helpResId = 0;
		if( Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT ) {
			helpResId = R.string.need_help_number_text;
		} else {
			helpResId = R.string.bank_need_help_number_text;	
		}
		
		//Create a one button modal with text as per parameters provided
		ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity, titleText, errorText, true, helpResId, R.string.ok);
		
		//If not logged in then exit the application
		if( !Globals.isLoggedIn() && HttpURLConnection.HTTP_UNAVAILABLE == errorCode) {
			if( Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "Dismiss will close the application [ErrorCode=errorCode]");
			}
			
			modal.setOnDismissListener(instance.new CloseApplicationOnDismiss(activity));
		}
		
		//Show one button error dialog 
		return modal;
	}
	

}
