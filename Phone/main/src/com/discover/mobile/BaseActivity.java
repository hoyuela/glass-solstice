
package com.discover.mobile;

import java.util.List;

import roboguice.activity.RoboActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.login.LockOutUserActivity;

/**
 * Base class for all SherlockActivities for our app
 * 
 * @author ekaram
 *
 */
public class BaseActivity extends RoboActivity implements ErrorHandlerUi{
	/**
	* Contains the last error that occurred with the activity. 
	* An object that holds a reference to an instance of BaseActivity can set its value by using setLastError.
	*/
	private int mLastError = 0;
	
    /**
     * Show a custom modal alert dialog for the activity
     * @param alert - the modal alert to be shown
     */
    public void showCustomAlert(final AlertDialog alert){
    	alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
    /**
     * Show the default one-button alert with a custom title, content an button text
     * 
     * Uses the orange button
     * 
     * @param title - the resource id for title for the alert
     * @param content - the resource id for content to display on the box
     * @param buttonText - the resource id for button text to display on the button
     */
    public void showOneButtonAlert(int title, int content, int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(this,title,content,buttonText));
    }
    
    /**
     * Show the default one-button alert with a custom title, content an button text
     * 
     * Uses the orange button
     * 
     * @param title - the resource id for title for the alert
     * @param content - the resource id for content to display on the box
     * @param buttonText - the resource id for button text to display on the button
     */
    public void showDynamicOneButtonAlert(int title, String content, int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(this,title,content,buttonText));
    }
    
    
    /**
	 * A common method used to forward user to error modal dialog with a given static
	 * string text message
	 * 
	 * @param errorCode HTTP error code
	 * @param errorText Text that is displayed in the content area of dialog
	 * @param titleText Text that is displayed at the top of the screen which describes the reason of the error
	 */
	public void sendToErrorPage(int errorCode, int titleText, int errorText) {
		//Create a modal dialog based on title, error text, and errorCode provided
		showCustomAlert(ErrorHandlerFactory.getInstance().createErrorModal(errorCode, titleText, errorText));
	}

	/**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	public void sendToErrorPage(int errorText) {
		final Intent maintenancePageIntent = new Intent((Context) this, LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
		startActivity(maintenancePageIntent);
	}

	/* 
	 * Child classes should override this to implement error handling behavior
	 * (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
	 */
	@Override
	public TextView getErrorLabel() {
		return null;
	}

	/*
	 * Child classes should override this to implement error handling behavior
	 *  (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
	 */
	@Override
	public List<EditText> getInputFields(){
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getContext()
	 */
	@Override
	public Context getContext() {
		return this;
	}
    
	/* (non-Javadoc)
	* @see com.discover.mobile.ErrorHandlerUi#setLastError()
	*/
	@Override
	public void setLastError(int errorCode) {
		mLastError = errorCode;
	}

	/* (non-Javadoc)
	* @see com.discover.mobile.ErrorHandlerUi#getLastError()
	*/
	@Override
	public int getLastError() {
		return mLastError;
	}
	

	@Override
	public void onResume(){
		super.onResume();
		
		//Load all application and user preferences from persistent storage
		Globals.loadPreferences(this);
		
		//Set this activity as the active activity
		ErrorHandlerFactory.getInstance().setActiveActivity(this);
	}
	
	/**
	 * 
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		//Save all application and user preferences into persistent storage
		Globals.savePreferences(this);
		
	}

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		return ErrorHandlerFactory.getInstance();
	}
	

}



