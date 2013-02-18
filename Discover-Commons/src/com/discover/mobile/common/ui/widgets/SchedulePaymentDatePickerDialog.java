package com.discover.mobile.common.ui.widgets;

import java.util.Calendar;

import android.content.Context;
import android.widget.DatePicker;

public class SchedulePaymentDatePickerDialog extends CustomDatePickerDialog {

	public SchedulePaymentDatePickerDialog(Context context,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		setTitle(getFormattedTitle(year, monthOfYear, dayOfMonth));
	}

	public SchedulePaymentDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		setTitle(getFormattedTitle(year, monthOfYear, dayOfMonth));
	}
	
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		setTitle(getFormattedTitle(year, month, day));
		super.onDateChanged(view, year, month, day);
	}

	private String getFormattedTitle(int year, int monthOfYear, int dayOfMonth) {
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		StringBuilder sb = new StringBuilder();
		sb.append(getDayOfWeekAbbreviation(cal.get(Calendar.DAY_OF_WEEK)));
		sb.append(", ");
		sb.append(getMonthAbbreviation(cal.get(Calendar.MONTH)));
		sb.append(" ");
		sb.append(cal.get(Calendar.DAY_OF_MONTH));
		sb.append(", ");
		sb.append(cal.get(Calendar.YEAR));
		
		return sb.toString();
	}
	
	private String getMonthAbbreviation(int monthOfYear) {
		switch (monthOfYear) {
		case Calendar.JANUARY:
			return "Jan";

		case Calendar.FEBRUARY:
			return "Feb";

		case Calendar.MARCH:
			return "Mar";

		case Calendar.APRIL:
			return "Apr";

		case Calendar.MAY:
			return "May";

		case Calendar.JUNE:
			return "June";

		case Calendar.JULY:
			return "July";
			
		case Calendar.AUGUST:
			return "Aug";
		
		case Calendar.SEPTEMBER:
			return "Sept";

		case Calendar.OCTOBER:
			return "Oct";
			
		case Calendar.NOVEMBER:
			return "Nov";
			
		case Calendar.DECEMBER:
			return "Dec";
			
		default:
			return "";
		}

	}

	private String getDayOfWeekAbbreviation(int dayOfWeek) {

		switch (dayOfWeek) {
		case Calendar.MONDAY:
			return "Mon";

		case Calendar.TUESDAY:
			return "Tues";

		case Calendar.WEDNESDAY:
			return "Wed";

		case Calendar.THURSDAY:
			return "Thur";

		case Calendar.FRIDAY:
			return "Fri";

		case Calendar.SATURDAY:
			return "Sat";

		case Calendar.SUNDAY:
			return "Sun";

		default:
			return "";
		}
	}

}
