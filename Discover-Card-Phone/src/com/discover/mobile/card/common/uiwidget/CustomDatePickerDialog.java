package com.discover.mobile.card.common.uiwidget;
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
	public CustomDatePickerDialog(final Context context, final int theme,
			final OnDateSetListener callBack, final int year, final int monthOfYear,
			final int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
	}

	public CustomDatePickerDialog(final Context context, final OnDateSetListener callBack, final int year, final int monthOfYear,
			final int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	/**
	 * This method is overridden because the default behavior of the date picker dialog is to 
	 * change its title as the date is changed. We want a static title. So it's overridden with
	 * no method body.
	 */
	@Override
	public void onDateChanged(final DatePicker view, final int year, final int month, final int day) {/*intentionally empty*/}

	/**
	 * This method looks for declared variables in the DatePickerDialog class that match
	 * known names for the day spinner. It then sets the visibility of the found element to GONE
	 * so that we get a nice looking month and year date picker dialog.
	 */
	public void hideDayPicker() {

		try{
			final Field[] datePickerDialogFields = DatePickerDialog.class.getDeclaredFields();
			for (final Field datePickerDialogField : datePickerDialogFields) { 
				final String datePickerDialogFieldName = datePickerDialogField.getName();
				if ("mDatePicker".equals(datePickerDialogFieldName) ||
						"mDateSpinner".equals(datePickerDialogFieldName)) {
					datePickerDialogField.setAccessible(true);
					final DatePicker datePicker = (DatePicker)datePickerDialogField.get(this);
					final Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
					for (final Field datePickerField : datePickerFields) {
						final String datePickerFieldName = datePickerField.getName();
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
		}catch(final Exception e){
			Log.d(TAG, "Error hiding day picker: " + e);
		}
	}

}
