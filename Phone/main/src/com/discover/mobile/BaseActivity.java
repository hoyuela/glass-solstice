
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
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.SharedPreferencesWrapper;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.login.LockOutUserActivity;

/**
 * Base class for all SherlockActivities for our app
 * 
 * @author ekaram
 *
 */
public class BaseActivity extends RoboActivity implements ErrorHandlerUi{
	
	
    /**
     * Save a boolean value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final boolean value){
    	SharedPreferencesWrapper.saveToSharedPrefs(this, key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default boolean value 
     */
    public boolean getValueFromSharedPrefs(final String key, final boolean defaultValue){
    	return SharedPreferencesWrapper.getValueFromSharedPrefs(this, key, defaultValue);
    }
    
    /**
     * Save a string value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public void saveToSharedPrefs(final String key, final String value){
    	SharedPreferencesWrapper.saveToSharedPrefs(this, key, value);
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default string value 
     */
    public String getValueFromSharedPrefs(final String key, final String defaultValue){
    	return SharedPreferencesWrapper.getValueFromSharedPrefs(this, key, defaultValue);
    }
    
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
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	public void sendToErrorPage(int titleText, int errorText) {
		final Intent maintenancePageIntent = new Intent((Context) this, LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
		startActivity(maintenancePageIntent);
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
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
    
   

}



