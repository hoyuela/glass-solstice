package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.widget.DatePicker;

public class CustomTitleDatePickerDialog extends CustomDatePickerDialog {

	public CustomTitleDatePickerDialog(Context context,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth, String title) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		setTitle(title);
	}

	public CustomTitleDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth, String title) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		setTitle(title);
	}
	
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
	}
}
