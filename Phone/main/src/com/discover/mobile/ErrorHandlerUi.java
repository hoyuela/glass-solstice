package com.discover.mobile;

import java.util.List;

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
	
	
	
}