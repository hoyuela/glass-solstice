package com.discover.mobile.common.ui.widgets;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caldroid.CaldroidFragment;
import com.caldroid.CaldroidGridAdapter;
import com.discover.mobile.common.R;

/**
 * Adapter class used for binding DateTime objects with the view displayed by a CalendarFragment
 * object. The DateTime objects provided in the constructor are used for determining how to 
 * render each cell in the CalendarFragment view. The cells will be rendered differently depending
 * whether they are part of the disableDates, selectedDates, or is earlier then the minDateTime or
 * later than the maxDateTime.
 * 
 * @author henryoyuela
 *
 */
public class CalendarAdapter extends CaldroidGridAdapter {
	public CalendarAdapter(final Context context, final int month, final int year,
			final ArrayList<DateTime> disableDates,
			final ArrayList<DateTime> selectedDates, final DateTime minDateTime,
			final DateTime maxDateTime) {
		super(context, month, year, disableDates, selectedDates, minDateTime,
				maxDateTime);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;

		// For reuse
		if (convertView == null) {
			cellView = inflater.inflate(R.layout.calendar_cell, null);
		}

		final int topPadding = cellView.getPaddingTop();
		final int leftPadding = cellView.getPaddingLeft();
		final int bottomPadding = cellView.getPaddingBottom();
		final int rightPadding = cellView.getPaddingRight();

		final TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
		final TextView tv2 = (TextView) cellView.findViewById(R.id.tv2);

		// Get dateTime of this cell
		final DateTime dateTime = this.datetimeList.get(position);
		final Resources resources = context.getResources();

		// Set color of the dates in previous / next month
		if (dateTime.getMonthOfYear() != month) {
			tv1.setTextColor(resources.getColor(R.color.caldroid_darker_gray));
		}


		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.isBefore(minDateTime))
				|| (maxDateTime != null && dateTime.isAfter(maxDateTime))
				|| (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

			tv1.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}
		}
	
		tv1.setText( " " +dateTime.getDayOfMonth());

		// Somehow after setBackgroundResource, the padding collapse. This is to recover the padding
		cellView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

		return cellView;
	}
}
