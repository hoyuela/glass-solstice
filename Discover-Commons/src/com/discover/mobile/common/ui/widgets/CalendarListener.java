package com.discover.mobile.common.ui.widgets;

import java.io.Serializable;
import java.util.Date;

import android.view.View;

import com.caldroid.CaldroidListener;

/**
 * Class used to subscribe for events from a CalendarFragment whenever a date has been selected 
 * by the user or the month has been changed via the navigation arrows in the header of the 
 * CalendarFragment.
 * 
 * @author henryoyuela
 *
 */
public abstract class CalendarListener extends CaldroidListener implements Serializable {
	/**
	 *  Auto-generated serial UID which is used to serialize and de-serialize CalendarListener objects
	 */
	private static final long serialVersionUID = 6662811694206780581L;
	/**
	 * Reference to CalendarFragment which owns and notifies this instance of the CalenderListener.
	 */
	final CalendarFragment calFrag;
	
	public CalendarListener(final CalendarFragment owner) {
		calFrag = owner;		
	}
	
	
	@Override
	public void onSelectDate(final Date date, final View view) {	
		calFrag.clearSelectedDates();
		
		/**Toggle the selected date from the current selected to the newly selected*/
		calFrag.setSelectedDates(date, date);
		
		calFrag.refreshView();
	}
	
	@Override
	public void onChangeMonth(final int month, final int year) {
		
		calFrag.updateDisplayedDate(month, year);
		
		/**Refresh the disable dates on the calendar after navigation between months on the calendar*/
 		calFrag.updateDisableDates(month-1, year);
		calFrag.refreshView();
	}
	
	public abstract void onCancel();

}
