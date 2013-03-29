package com.discover.mobile.card.account.recent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.services.account.recent.RecentActivityPeriodDetail;
public class ChoosePeriodItem extends RelativeLayout{



	/**Period associated with this item*/
	private final RecentActivityPeriodDetail period;

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 * @param period - time period to associate with this item
	 */
	public ChoosePeriodItem(final Context context, final AttributeSet attrs, final RecentActivityPeriodDetail period) {
		super(context, attrs);

		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.choose_period_item, null);

		/**Label associated with the item*/
		final TextView label = (TextView) mainView.findViewById(R.id.date_string);
		label.setText(period.displayDate);
		this.period = period;

		addView(mainView);
	}

	/**
	 * Get the period associated with this item
	 * @return the period associated with this item
	 */
	public RecentActivityPeriodDetail getPeriod() {
		return period;
	}
}
