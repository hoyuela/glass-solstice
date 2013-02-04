
package com.discover.mobile;

import java.util.List;

import roboguice.activity.RoboActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
public class BaseActivity extends RoboActivity implements ErrorHandlerUi, AlertDialogParent{
	private static final String TAG = BaseActivity.class.getSimpleName();
	/**
	* Contains the last error that occurred with the activity. 
	* An object that holds a reference to an instance of BaseActivity can set its value by using setLastError.
	*/
	private int mLastError = 0;
	/**
	 * Reference to the dialog currently being displayed on top of this activity. Is set using setDialog();
	 */
	private AlertDialog mActiveDialog;
	
    /**
     * Show a custom modal alert dialog for the activity
     * @param alert - the modal alert to be shown
     */
    @Override
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
    @Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText){    	
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
    @Override
	public void showDynamicOneButtonAlert(final int title, final String content, final int buttonText){    	
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
	public void sendToErrorPage(final int errorCode, final int titleText, final int errorText) {
		//Create a modal dialog based on title, error text, and errorCode provided
		showCustomAlert(ErrorHandlerFactory.getInstance().createErrorModal(errorCode, titleText, errorText));
	}

	/**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	public void sendToErrorPage(final int errorText) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
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
	public void setLastError(final int errorCode) {
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
		ActivityManager.setActiveActivity(this);
	}
	
	/**
	 * 
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		//Save all application and user preferences into persistent storage
		Globals.savePreferences(this);
		
		closeDialog();
	}
	

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		return ErrorHandlerFactory.getInstance();
	}

	/**
	 * @return Return a reference to the current dialog being displayed over this activity.
	 */
	@Override
	public AlertDialog getDialog() {
		return mActiveDialog;
	}

	/**
	 * Allows to set the current dialog that is being displayed over this activity.
	 */
	@Override
	public void setDialog(final AlertDialog dialog) {
		mActiveDialog = dialog;
		
	}

	/**
	 * Closes the current dialog this is being displayed over this activity. Requires
	 * a call to setDialog to be able to use this function.
	 */
	@Override
	public void closeDialog() {
		if( mActiveDialog != null && mActiveDialog.isShowing()) {
			mActiveDialog.dismiss();
			mActiveDialog = null;
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Activity does not have a dialog associated with it!" );
			}
		}
		
	}
	
	/**
	 * Starts a Progress dialog using this activity as the context. The ProgressDialog created
	 * will be set at the active dialog.
	 */
	@Override
	public void startProgressDialog() {		
		if( mActiveDialog == null ) {
			mActiveDialog = ProgressDialog.show(this,"Discover", "Loading...", true);	
			setDialog(mActiveDialog);
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Activity does not have a dialog associated with it!" );
			}
		}
	}

}


