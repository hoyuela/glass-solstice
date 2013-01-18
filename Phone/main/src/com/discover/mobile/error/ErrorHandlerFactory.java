package com.discover.mobile.error;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.login.LoginActivity;

/**
 * Used to build error dialogs used throughout the application.
 * 
 * @author henryoyuela
 *
 */
public class ErrorHandlerFactory {
	static Activity mActivity;
	static final String TAG = ErrorHandlerFactory.class.getSimpleName();
	static final ErrorHandlerFactory instance = new ErrorHandlerFactory();
	
	/**
	 * Uses a singleton design pattern 
	 */
	private ErrorHandlerFactory() {
	}
	
	public static ErrorHandlerFactory getInstance() {
		return instance;
	}
	
	public void setActiveActivity(Activity activity) {
		mActivity = activity;
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
		
		public CloseApplicationOnDismiss(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if( Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "Closing Application...");
			}
			
			//close application
			this.activity.finish();
			
		}
	}
	
	/**
	 * DismissListener which can be applied to an alert dialog to navigate back to the 
	 * login page when it is dismissed.
	 * 
	 * @author henryoyuela
	 *
	 */
	private class NavigateToLoginOnDismiss implements OnDismissListener {
		//Reference of the activity to be able to close out the application
		private final Activity activity;
		
		public NavigateToLoginOnDismiss(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			//Send an intent to open login activity if current activity is not login
			if( this.activity.getClass() != LoginActivity.class ) {
				final Intent intent = new Intent(activity, LoginActivity.class);
				final Bundle bundle = new Bundle();
				bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
				intent.putExtras(bundle);
				activity.startActivity(intent);
				
			} else {
				if( Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "Application is already in login view");
				}
			}
			
			//Close current activity
			activity.finish();
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
	public ModalAlertWithOneButton createErrorModal(int errorCode, int titleText, int errorText) {
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
		ModalAlertWithOneButton modal = new ModalAlertWithOneButton(mActivity, titleText, errorText, true, helpResId, R.string.ok);
		
		//If not logged in then exit the application
		if( !Globals.isLoggedIn() && HttpURLConnection.HTTP_UNAVAILABLE == errorCode) {	
			//Close application
			modal.setOnDismissListener(instance.new CloseApplicationOnDismiss(mActivity));
		} else if( Globals.isLoggedIn() ) {
			//Navigate back to login
			modal.setOnDismissListener(instance.new NavigateToLoginOnDismiss(mActivity));
		}
		
		//Show one button error dialog 
		return modal;
	}
	
	//HTTP 500 Internal Server Error
	public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {
		
		return createErrorModal(HttpURLConnection.HTTP_INTERNAL_ERROR, R.string.error_500_title, R.string.bank_error_500_message);
	
	}
	
	//HTTP 500 Internal Server Error
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(Activity activity) {
		
		return createErrorModal(HttpURLConnection.HTTP_INTERNAL_ERROR, R.string.error_503_title, R.string.bank_error_503_message);
	
	}
	
	public void handleHttpForbiddenErrorModal() {
		
		
	}

	public void handleGenericErrorModal(int httpErrorCode) {
		
		
	}

	public void handleHttpUnauthorized() {
		/*
		setErrorText(R.string.login_error);
		setInputFieldsDrawableToRed();
		clearInputs();	
		*/
	}
	
	public void handleHttpUnavailableErrorModal(boolean planned) {
		
		
	}
	
	public void handleLoginOrStrongAuthFailure(String errorMessage ) {
		/*
		setErrorText(R.string.login_error);
		setInputFieldsDrawableToRed();
		clearInputs();	
		*/
		
		//TODO: Update Strong Auth Class to inherit from ErrorHandlerUI and add these methods
	}

	public void handleLockedOut() {
		//TODO: Show Locked Out Modal here
		
	}
}
