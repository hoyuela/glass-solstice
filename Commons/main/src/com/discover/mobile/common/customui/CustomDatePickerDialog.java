package com.discover.mobile.common.customui;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

public class CustomDatePickerDialog extends DatePickerDialog{
	private static final String TAG = CustomDatePickerDialog.class.getSimpleName();
	
	public CustomDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
	}
	
	public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
	
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		/*intentionally empty*/
	}

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
