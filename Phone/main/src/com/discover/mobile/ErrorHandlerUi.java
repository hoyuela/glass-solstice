package com.discover.mobile;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;


/**
 * An interface to support common error handling across activities/fragments
 * 
 * @author ekaram
 *
 */
public interface ErrorHandlerUi {

	/**
	 * returns the error label which the error handler will use to display error text
	 * @return
	 */
	public TextView getErrorLabel();
	
	/**
	 * returns a list of input fields to highlight red or clear on error
	 * @return
	 */
	public List<EditText> getInputFields();
	
	/**
	 * Display a custom alert 
	 * @param alert
	 */
	public void showCustomAlert(final AlertDialog alert);
	
	/**
	 * Show a one button modal dialog 
	 * 
	 * @param title
	 * @param content
	 * @param buttonText
	 */
	public void showOneButtonAlert(int title, int content, int buttonText);
	
	/**
	 * Show a one button dialog that accepts dynamic string of body text 
	 * (not resource text)
	 * 
	 * To be used if we ever want to directly display an error message from
	 * a third party, instead of using a canned resource
	 * 
	 * 
	 * @param title
	 * @param content
	 * @param buttonText
	 */
	public void showDynamicOneButtonAlert(int title, String content, int buttonText);
	
	/**
	 * Send to error page; display error text with given title
	 * @param titleText
	 * @param errorText
	 */
	
	/**
	 * A common method used to forward user to error modal dialog with a given static
	 * string text message
	 * 
	 * @param errorCode HTTP error code
	 * @param errorText Text that is displayed in the content area of dialog
	 * @param titleText Text that is displayed at the top of the screen which describes the reason of the error
	 */
	public void sendToErrorPage(int errorCode, int titleText, int errorText);
	
	/**
	 * Send to error page; display error text with default error page title
	 * @param errorText
	 */
	public void sendToErrorPage(int errorText);
	
	/**
	 * returns the context associated with this error handler ui.
	 * 
	 * for fragment, this is the activityfragment.
	 * for activities, this is itself
	 * 
	 * @return
	 */
	public Context getContext();
	
	/**
	* Sets the last error that occurred 
	* @param errorCode
	*/
	public void setLastError(int errorCode);
	
	/**
	* Returns the value of the last error code set using setLastError
	* 
	* @return the cached last error code
	*/
	public int getLastError();
	
}