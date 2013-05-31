package com.discover.mobile.bank.account;

import java.util.Calendar;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

/**
 * A runnable which will update a given text label with a Name and the current salutation based on the time of day.
 * For example it will set the text of the salutation text label to 
 * "Good morning, {firstName}" if the time of day is between 12:00am and 11:59am.
 *
 * @author scottseward
 *
 */
public final class SalutationUpdater implements Runnable {
	final static int MORNING_START = 0;
	final static int MORNING_END = 12;
	final static int AFTERNOON_START = MORNING_END;
	final static int AFTERNOON_END = 18;
	final static int EVENING_START = AFTERNOON_END;
	final static int EVENING_END = 24;
	
	private TextView salutation = null;
	private BankAccountSummaryFragment currentContext = null;
	private Handler uiHandler = null;
	private String firstName = StringUtility.EMPTY;
	
	public SalutationUpdater(final TextView salutation, final String firstName, final BankAccountSummaryFragment context) {
		uiHandler = new Handler(Looper.getMainLooper());
		this.salutation = salutation;
		this.currentContext = context;
		this.firstName = firstName;
	}
	
	/**
	 * Updates the salutation text label to the current greeting.
	 */
	@Override
	public void run() {
		final int hourOfDayMilitary = Calendar.getInstance(Locale.US).get(Calendar.HOUR_OF_DAY);
		final StringBuilder greetingBuilder = new StringBuilder();
		String greetingSuffix = null;
		
		final String greetingPrefix = currentContext.getResources().getString(R.string.greeting_prefix);

		greetingSuffix = getGreetingSuffixForHour(hourOfDayMilitary);

		if(greetingPrefix != null && !Strings.isNullOrEmpty(greetingSuffix)) {
			greetingBuilder.append(String.format(greetingPrefix, greetingSuffix));
			greetingBuilder.append(StringUtility.SPACE);
		}
		
		if(!Strings.isNullOrEmpty(firstName)) {
			greetingBuilder.append(firstName);
		}
		
		final Runnable nameUpdateRunnable = new Runnable() {
			@Override
			public void run() {
				if(salutation != null) {
					salutation.setText(greetingBuilder.toString());
					currentContext.updateDropdownPosition();
				}
			}
		};
		
		uiHandler.post(nameUpdateRunnable);
	}
	
	/**
	 * 
	 * @param hourOfDayMilitary an integer that represents the hour of day on a 24 hour cycle.
	 * @return a String for the time of day, "morning", "evening", or "afternoon".
	 */
	private String getGreetingSuffixForHour(final int hourOfDayMilitary) {
		String greetingSuffix = StringUtility.EMPTY;
		int greetingResource = 0;
		
		if(isBetweenTime(MORNING_START, hourOfDayMilitary, MORNING_END)) {
			greetingResource = R.string.greeting_morning_suffix;
		}else if (isBetweenTime(AFTERNOON_START, hourOfDayMilitary, AFTERNOON_END)) {
			greetingResource = R.string.greeting_afternoon_suffix;
		}else if (isBetweenTime(EVENING_START, hourOfDayMilitary, EVENING_END)) {
			greetingResource = R.string.greeting_evening_suffix;
		}
		
		if(greetingResource != 0) {
			greetingSuffix = currentContext.getResources().getString(greetingResource);
		}
		
		return greetingSuffix;
	}
	
	/**
	 * Checks to see if a provided time of day {hour} is within the range of the start and end times. 
	 * The start time is inclusive while the end time is exclusive.
	 * 
	 * @param start the earliest time that the parameter hour should be equal to or greater than.
	 * @param hour the hour of day.
	 * @param end the hour of day that the hour parameter will be checked to be before
	 * @return if the hour of day is between the start and end values. Inclusive to start and exclusive to end.
	 */
	private boolean isBetweenTime(final int start, final int hour, final int end) {
		return start <= hour && hour < end;
	}
	
	public static boolean shouldUpdateGreeting() {
		final int hourOfDayMilitary = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		boolean shouldUpdateGreeting = false;
		
		shouldUpdateGreeting |= hourOfDayMilitary == MORNING_START;
		shouldUpdateGreeting |=	hourOfDayMilitary == AFTERNOON_START;
		shouldUpdateGreeting |= hourOfDayMilitary == EVENING_START;
		
		return shouldUpdateGreeting;
	}
}
