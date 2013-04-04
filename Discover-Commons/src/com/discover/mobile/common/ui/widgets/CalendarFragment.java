package com.discover.mobile.common.ui.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caldroid.CaldroidFragment;
import com.caldroid.CaldroidGridAdapter;

/**
 * Class used to display a Calendar on top of an existing Fragment Activity. This class
 * is based on the Caldroid library object CalroidFragment.
 * 
 * Example on how to use this class from a Fragment Activity:
 * 
 * 
 *  //Create listener
 *	final CalendarListener calendarListener = new CalendarListener(calendarFragment) {
 *		private static final long serialVersionUID = -5277452816704679940L;
 *
 *		@Override
 *		public void onSelectDate(final Date date, final View view) {
 *			super.onSelectDate(date, view);
 *			
 *			final Calendar cal=Calendar.getInstance();
 *			cal.setTime(date);
 *			setChosenPaymentDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
 *			
 *			calendarFragment.dismiss();
 *		}
 *	};
 * 	CalendarFragment calendarFragment = new CalendarFragment();
 *		
 *	calendarFragment.show(getFragmentManager(),
 *					      "Title of Calendar",
 *						  earliestPaymentDate, 
 *						  disabledDates,
 *						  calendarListener);
 * 
 * @author henryoyuela
 *
 */
public class CalendarFragment extends CaldroidFragment {
	/**
	 * Key for holding the header title of the fragment used for rotation handling
	 */
	public static final String DIALOG_TITLE = "dialogTitle";
	/**
	 * Key for holding the current month displayed on the fragment used for rotation handling
	 */
	public static final String MONTH = "month";
	/**
	 * Key for holding the year displayed on the fragment used for rotation handling
	 */
	public static final String YEAR = "year";
	/**
	 * Key for holding the minimum selectable date from the calendar used for rotation handling
	 */
	public static final String MIN_DATE = "min-date";
	/**
	 * Key for holding the current date selected on the calendar fragment used for rotation handling
	 */
	public static final String EARLIEST_DATE = "earliest-date";
	/**
	 * Key for holding list of non-selectable dates on the calendar used for rotation handling
	 */
	public static final String DISABLED_DATES = "disabled-dates";
	/**
	 * Key for hold the tag used to lookup this fragment in the fragment manager provided via the show method.
	 */
	public static final String TAG = "CALDROID_DIALOG_FRAGMENT";
	
	/**
	 * Reference to first selectable date on the calendar
	 */
	protected Calendar earliestDate;
	/**
	 * Reference to the list of non-selectable dates on the calendar
	 */
	protected ArrayList<Date> holidays;
	
	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter() {
		return new CalendarAdapter(getActivity(), month, year, disableDates, selectedDates, minDateTime, maxDateTime);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Check if onCreateView is called because of rotation*/
		if( null != savedInstanceState ) {
			updateData(getArguments());
		}
		
		return view;
	}
	
	@Override
	public void onSaveInstanceState(final Bundle outstate) {
		super.onSaveInstanceState(outstate);
	}
	
	
	/**
	 * Method used to display the fragment as a dialog on top of the existing activity.
	 * 
	 * @param manager Reference to the FragmentManager of the current FragmentActivity displayed on the application.
	 * @param title Title to display in the header of the dialog.
	 * @param date The highlighted first selectable date on the calendar.
	 * @param disabledDates The list of dates to be considered non-selectable on the calendar.
	 * @param listener Reference to listern to receive selected date and change month notifications
	 */
	public void show(final FragmentManager manager, final String title, final Calendar date, final ArrayList<Date> disabledDates, final CalendarListener listener) {		
	
		/**Set title, month and year of calendar using bundle*/
		final Bundle args = new Bundle();
		args.putString(CalendarFragment.DIALOG_TITLE, title);
		args.putInt(MONTH, date.get(Calendar.MONTH) + 1);
		args.putInt(YEAR, date.get(Calendar.YEAR));
		args.putSerializable(EARLIEST_DATE, date);
		args.putSerializable(MIN_DATE, date.getTime());
		args.putSerializable(DISABLED_DATES, disabledDates);
		setArguments(args);
		
		updateData(args);
		
		setCaldroidListener(listener);
		
		show(manager,TAG);
	}
	
	/**
	 * Method used to retreive the list of dates that should be disabled on the month actively 
	 * being displayed to the user, including the list of disabled dates passed in the show method.
	 * 
	 * @param month Month that is being used to generate list of disabled dates
	 * @param year Year that is being used to generate list of disabled dates
	 * 
	 * @return Returns list of disabled dates for the month before and month after the month specified. 
	 *         In addition to the disable dates for the month specified.
	 */
	private ArrayList<Date> getUnavailableDates(final int month, final int year) {
		final Calendar activeDate = Calendar.getInstance();
		activeDate.add(Calendar.MONTH, month - activeDate.get(Calendar.MONTH) - 1);
		activeDate.set(Calendar.YEAR, ((activeDate.get(Calendar.MONTH) == 11)? year - 1 : year));
	
		final Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.MONTH, month - endDate.get(Calendar.MONTH) + 1);
		endDate.add(Calendar.DATE, endDate.get(Calendar.DAY_OF_MONTH) + 7);
		endDate.set(Calendar.YEAR, ((endDate.get(Calendar.MONTH) == 0)? year + 1 : year));
		
		final ArrayList<Date> dates = (holidays == null) ? new ArrayList<Date>() : holidays;
		
		while( activeDate.compareTo(endDate) <= 0 ) {
			printDate(activeDate);
			
			if( activeDate.compareTo(earliestDate) >= 0 ) {
				if( isWeekend(activeDate) ) {
					dates.add(activeDate.getTime());
	
				}
			} else {
				dates.add(activeDate.getTime());
			}
			
			activeDate.add(Calendar.DATE, 1);
		}
		
		return dates;
	}
	
	/**
	 * Utility method for debugging purposes only.
	 * 
	 * @param cal
	 */
	private void printDate(final Calendar cal) {
		Log.v("Discover", cal.get(Calendar.MONTH) +"/" +
						  cal.get(Calendar.DATE) +"/" +
						  cal.get(Calendar.YEAR) +"-" +
						  cal.get(Calendar.DAY_OF_WEEK) +"\n");
	}
	
	/**
	 * Method used to update the view of the calendar. Used at initial creation
	 * and on rotation.
	 * 
	 * @param bundle Holds date values used to configure the calendar.
	 */
	@SuppressWarnings("unchecked")
	private void updateData(final Bundle bundle) {
		earliestDate = (Calendar) bundle.get(EARLIEST_DATE);
		holidays = (ArrayList<Date>) bundle.getSerializable(DISABLED_DATES);
		
		final int month = bundle.getInt(MONTH) - 1;
		final int year = bundle.getInt(YEAR);
		
		final Date minDate = (Date) bundle.get(MIN_DATE);
		setMinDate(minDate);
				
		/**Disable weekend dates starting from earliest date provided*/
		updateDisableDates(month, year);
		
		
	}

	/**
	 * Method used to update the displayed calendar with the disable dates for
	 * the month and year specified.
	 * 
	 * @param month Month used to generate list of disabled dates.
	 * @param year Year used to generate list of disable dates.
	 */
	public void updateDisableDates(final int month, final int year) {
		final ArrayList<Date> disableDates = getUnavailableDates(month, year);		
		
		setDisableDates(disableDates);
	}
	
	/**
	 * Method used to check if the date represented by cal is a weekend or not.
	 * 
	 * @param cal Holds date whose day of the week is being check to see if it is a weekend.
	 * 
	 * @return True if cal holds the date to a weekend, false otherwise.
	 */
	public boolean isWeekend(final Calendar cal) {
		final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		return (Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek);	
	}

}
