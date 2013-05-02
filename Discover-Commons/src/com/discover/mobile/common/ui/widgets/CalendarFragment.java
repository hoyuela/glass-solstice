package com.discover.mobile.common.ui.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.caldroid.CaldroidFragment;
import com.discover.mobile.common.R;

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
	public static final String SELECTED_DATE = "selected-date";
	/**
	 * Key for holding list of non-selectable dates on the calendar used for rotation handling
	 */
	public static final String DISABLED_DATES = "disabled-dates";
	/**
	 * Key for hold the tag used to lookup this fragment in the fragment manager provided via the show method.
	 */
	public static final String TAG = "CALDROID_DIALOG_FRAGMENT";
	/**
	 * Static variables that holds the number of days in a week
	 */
	public static final int NUMBER_OF_DAYS_IN_WEEK = 7;
	
	/**
	 * Reference to the current chosen date on the calendar at start-up
	 */
	private Calendar selectedDate;
	/**
	 * Reference to first selectable date on the calendar
	 */
	private Calendar minDate;
	/**
	 * Reference to the list of non-selectable dates on the calendar
	 */
	private ArrayList<Date> holidays;
	/**
	 * Reference to text view that displays at the top of the fragment as the title
	 */
	private TextView titleTxtVw;
	/**
	 * Integer that holds the month that is currently being displayed on the calendar. Used to handle rotation.
	 */
	private int displayedMonth;
	/**
	 * Integer that holds the year that is currently being displayed on the calendar. Used to handle rotation.
	 */
	private int displayedYear;
	/**
	 * Reference to listener that will receive events when there is a change in month or selected date from the calendar.
	 */
	private CalendarListener eventListener;
	
	
	@Override
	public void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * Initialize month and year that will be displayed on the calendar. Note that the fragment retains it's state on
		 * rotation so this onCreate is only called once and not called on rotation.
		 */
		displayedMonth = getArguments().getInt(MONTH);
		displayedYear = getArguments().getInt(YEAR);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Check if onCreateView is called because of rotation*/
		if( null != savedInstanceState ) {
			updateData(getArguments());
		}
		
		/**Create Calendar Header*/
		createHeader(view, container, inflater);
		
		return view;
	}
	
	/**
	 * Method used to apply a custom header and replace the dialog header
	 * @param view
	 * @param container
	 * @param inflater
	 */
	public void createHeader(final View view, final ViewGroup container, final LayoutInflater inflater) {
		/** Hide Dialog Header */
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		

		/**Add Custom Header to the top of the Calendar*/
		final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.calendar_title_view);
		final TextView monthTitle = (TextView) layout.findViewById(R.id.calendar_month_year_textview);
		final LinearLayout titleHeader = (LinearLayout) inflater.inflate(R.layout.calendar_title_header, container, false);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
																					RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		titleHeader.setLayoutParams(params);
		layout.addView(titleHeader);
		
		params =  (LayoutParams) monthTitle.getLayoutParams();
		params.addRule(RelativeLayout.BELOW, titleHeader.getId());
		monthTitle.setLayoutParams(params);
		
		final Button leftArrow = (Button)view.findViewById(R.id.calendar_left_arrow);
		params =  (LayoutParams)leftArrow.getLayoutParams();
		params.addRule(RelativeLayout.BELOW, titleHeader.getId());
		leftArrow.setLayoutParams(params);
		
		final Button rightArrow = (Button)view.findViewById(R.id.calendar_right_arrow);
		params =  (LayoutParams)rightArrow.getLayoutParams();
		params.addRule(RelativeLayout.BELOW, titleHeader.getId());
		rightArrow.setLayoutParams(params);
		
		/**Set Text for the Header*/
		titleTxtVw = (TextView)titleHeader.findViewById(R.id.title);
		titleTxtVw.setText(this.getArguments().getString(CalendarFragment.DIALOG_TITLE));
	}
	
	/**
	 * Method used to the listener that will receive events when there is a change in month or selected date 
	 * from the calendar.
	 * 
	 * @param listener Reference to a CalendarListener object
	 */
	public void setCalendarListener(final CalendarListener listener) {
		eventListener = listener;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/**Update calendar with month and year that was displayed prior to rotation*/
		final Calendar current = Calendar.getInstance();
		current.set(Calendar.MONTH, displayedMonth - 1);
		current.set(Calendar.YEAR, displayedYear);
		this.moveToDate(current.getTime());
		
		/**Set listener here to avoid updates while calendar is being initialized*/
		setCaldroidListener(eventListener);
	}
	
	@Override
	public void onPause() {
		/**Set listener to null here to avoid updates while the calendar is being rotated*/
		setCaldroidListener(null);
		
		super.onPause();
	}

	/**
	 * Method used to display the fragment as a dialog on top of the existing activity.
	 * 
	 * @param manager Reference to the FragmentManager of the current FragmentActivity displayed on the application.
	 * @param title Title to display in the header of the dialog.
	 * @param date The highlighted first selectable date on the calendar.
	 * @param displayedDate Holds the month and year the calendar should display.
	 * @param disabledDates The list of dates to be considered non-selectable on the calendar.
	 * @param listener Reference to listern to receive selected date and change month notifications
	 */
	public void show(final FragmentManager manager, final String title, 				 
					 final Calendar displayedDate,
					 final Calendar selectedDate,
					 final Calendar minDate, 
					 final ArrayList<Date> disabledDates, 
					 final CalendarListener listener) {		
	
		/**Set title, month and year of calendar using bundle*/
		final Bundle args = new Bundle();
		args.putString(CalendarFragment.DIALOG_TITLE, title);
		args.putInt(MONTH, displayedDate.get(Calendar.MONTH) + 1);
		args.putInt(YEAR, displayedDate.get(Calendar.YEAR));
		args.putSerializable(SELECTED_DATE, selectedDate);
		args.putSerializable(MIN_DATE, minDate);
		args.putSerializable(DISABLED_DATES, disabledDates);
		args.putBoolean("fitAllMonths", false);
		setArguments(args);
		
		updateData(args);
		
		eventListener = listener;
		
	
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
		activeDate.set(Calendar.YEAR, ((activeDate.get(Calendar.MONTH) == Calendar.DECEMBER)? year - 1 : year));
	
		final Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.MONTH, month - endDate.get(Calendar.MONTH) + 1);
		endDate.add(Calendar.DATE, endDate.get(Calendar.DAY_OF_MONTH) + NUMBER_OF_DAYS_IN_WEEK);
		endDate.set(Calendar.YEAR, ((endDate.get(Calendar.MONTH) == 0)? year + 1 : year));
		
		final ArrayList<Date> dates = (holidays == null) ? new ArrayList<Date>() : holidays;
		
		while( activeDate.compareTo(endDate) <= 0 ) {
			if( activeDate.compareTo(minDate) >= 0 ) {
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
	 * Method used to update the view of the calendar. Used at initial creation
	 * and on rotation.
	 * 
	 * @param bundle Holds date values used to configure the calendar.
	 */
	@SuppressWarnings("unchecked")
	private void updateData(final Bundle bundle) {
		selectedDate = (Calendar) bundle.get(SELECTED_DATE);
		holidays = (ArrayList<Date>) bundle.getSerializable(DISABLED_DATES);
		
		/**Android Calendar Object starts month with an index of 0 therefore must subtract 1*/
		final int month = bundle.getInt(MONTH) - 1;
		final int year = bundle.getInt(YEAR);
		
		/**Dates earlier than this will appear grayed out and unselectable*/
		minDate = (Calendar) bundle.get(MIN_DATE);
		setMinDate(minDate.getTime());
		/** Restrict the maximum date that a user can select to one year from today */
		final Calendar yearFromTodayCalendar = Calendar.getInstance();
		yearFromTodayCalendar.add(Calendar.YEAR, 1);
		setMaxDate(yearFromTodayCalendar.getTime());
		/**Specify which dates should appear selected on calendar*/
		this.setSelectedDates(selectedDate.getTime(), selectedDate.getTime());
				
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
	

	/**
	 * Method used to update the displayed month and year. This method does not update the UI, only stores the values
	 * such that they are read on rotation and update the UI according to the values provided in the parameter list.
	 * This method is not meant to be used by a hosting application but by a CalenderListener.
	 * 
	 * @param displayedMonth Current Month being displayed on the calendar.
	 * @param displayedYear Current Year being displayed on the calendar.
	 */
	public void updateDisplayedDate(final int displayedMonth, final int displayedYear) {
		this.displayedMonth = displayedMonth;
		this.displayedYear = displayedYear;
	}
	
	
	/**
	 * Returns a calendar which starts at the first possible valid day from it's current day and on.
	 * @param currentCalendar a calendar object which has its current date set to some value.
	 * @param holidays a list of Date objects which are not valid for the returned calendar to have selected.
	 * @return a Calendar who's current day is the first valid day of the currentCalendar, that is 
	 * not a weekend or holiday.
	 */
	public static Calendar getFirstValidDateCalendar(final Calendar currentCalendar, final List<Date> holidays) {
		final Calendar hasNextValidDateCal = currentCalendar;
		
		if(hasNextValidDateCal != null) {	
			//Get the current day so we can see if its a weekend
			int currentDayOfWeek = hasNextValidDateCal.get(Calendar.DAY_OF_WEEK);
			
			//While today is a weekend, increment today to the next day.
			while (currentDayOfWeek == Calendar.SATURDAY || 
					currentDayOfWeek == Calendar.SUNDAY ||
					isHoliday(hasNextValidDateCal, holidays)) {
				hasNextValidDateCal.add(Calendar.DAY_OF_MONTH, 1);
				currentDayOfWeek = hasNextValidDateCal.get(Calendar.DAY_OF_WEEK);
			}
		}
		
		return hasNextValidDateCal;
	}
	
	/**
	 * Checks the current day of a given calendar against a list of holiday dates. Returns if the selected day is
	 * a holiday.
	 * @param calendar a calendar who's current date needs to be compared against a list of holidays.
	 * @param holidays a list of Date objects that represent holidays that the calendar is not allowed to use.
	 * @return if the current date of the calendar is a holiday.
	 */
	public static boolean isHoliday(final Calendar calendar, final List<Date> holidays) {
		boolean isTodayHoliday = false;
		
		if(calendar != null && holidays != null && holidays.size() > 0) {
			final Calendar holidayCalendar = Calendar.getInstance();
			//Compare each valid holiday date against the passed holiday date
			for(final Date holiday : holidays) {
				holidayCalendar.setTime(holiday);
	
				isTodayHoliday |= holidayCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
					holidayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
					holidayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
			}
		}
		
		return isTodayHoliday;
	}

}
