package com.discover.mobile.common.customui;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

/**
 * This custom date picker extends the default android date picker dialog.
 * It is extended in this case for the purpose of removing the day spinner from the dialog.
 * This class simply hides the day picker and only presents the month and year.
 * 
 * @author scottseward
 *
 */
public class CustomDatePickerDialog extends DatePickerDialog{
	private static final String TAG = CustomDatePickerDialog.class.getSimpleName();
	
	/**
	 * Default constructors.
	 */
	public CustomDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
	}
	
	public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	/**
	 * This method is overridden because the default behavior of the date picker dialog is to 
	 * change its title as the date is changed. We want a static title. So it's overridden with
	 * no method body.
	 */
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {/*intentionally empty*/}

	/**
	 * This method looks for declared variables in the DatePickerDialog class that match
	 * known names for the day spinner. It then sets the visibility of the found element to GONE
	 * so that we get a nice looking month and year date picker dialog.
	 */
	public void hideDayPicker() {
		
		 try{
			    Field[] datePickerDialogFields = DatePickerDialog.class.getDeclaredFields();
			    for (Field datePickerDialogField : datePickerDialogFields) { 
			    	String datePickerDialogFieldName = datePickerDialogField.getName();
			        if ("mDatePicker".equals(datePickerDialogFieldName) ||
			        	"mDateSpinner".equals(datePickerDialogFieldName)) {
			            datePickerDialogField.setAccessible(true);
			            DatePicker datePicker = (DatePicker)datePickerDialogField.get(this);
			            Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
			            for (Field datePickerField : datePickerFields) {
			            	String datePickerFieldName = datePickerField.getName();
			               if ("mDayPicker".equals(datePickerFieldName) ||
			            		   "mDaySpinner".equals(datePickerFieldName)) {
			                  datePickerField.setAccessible(true);
			                  Object dayPicker = new Object();
			                  dayPicker = datePickerField.get(datePicker);
			                  ((View) dayPicker).setVisibility(View.GONE);
			               }
			            }
			         }
			      }
			    }catch(Exception e){
			    	Log.d(TAG, "Error hiding day picker: " + e);
			    }
		}

}
