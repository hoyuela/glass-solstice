package com.discover.mobile.section.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.account.recent.RecentActivityPeriodDetail;

public class ChoosePeriodItem extends LinearLayout{
	
	/**Label associated with the item*/
	private final TextView label;
	
	private final RecentActivityPeriodDetail detail;
	
	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public ChoosePeriodItem(final Context context, final AttributeSet attrs, final RecentActivityPeriodDetail detail) {
		super(context, attrs);
		
		final LinearLayout mainView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.choose_period_item, null);
		
		label = (TextView) mainView.findViewById(R.id.date_string);
		label.setText(detail.displayDate);
		this.detail = detail;
		
		addView(mainView);
	}

	public RecentActivityPeriodDetail getDetail() {
		return detail;
	}
}
