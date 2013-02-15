package com.discover.mobile.bank.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;

/**
 * Adapter used to display data on the account activity table
 * @author jthornton
 *
 */
public class BankListAdapter extends ArrayAdapter<List<ActivityDetail>>{

	/**List of details to show*/
	private List<ActivityDetail> details;

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	/**Resources of the application*/
	private final Resources res;

	/*Fragment currently displayed*/
	private final BankAccountActivityTable fragment;

	/**
	 * Constuctor for the adapter
	 * @param context - activity context
	 * @param textViewResourceId - resource
	 * @param items - items to set in the adapter
	 * @param fragment - fragment using the adapter
	 */
	public BankListAdapter(final Context context, final int textViewResourceId, final List<ActivityDetail> items, 
			final BankAccountActivityTable fragment) {
		super(context, textViewResourceId);
		details = items;
		inflater = LayoutInflater.from(context);
		res = context.getResources();
		this.fragment =fragment;
	}

	/**
	 * Get the view
	 * @param postion - current position
	 * @param view - current view
	 * @param parent - parent view group
	 */
	@Override
	public View getView(final int position, View view, final ViewGroup parent){
		ViewHolder holder = null;

		final ActivityDetail detail = details.get(position);

		/**At the end of the list try loading more*/
		if(position == this.getCount()-1){
			fragment.maybeLoadMore();
		}

		/**If the view is null, create a new one*/
		if(null == view || !(view.getTag() instanceof ViewHolder)){
			holder = new ViewHolder();
			if(null != detail){
				view = inflater.inflate(R.layout.bank_table_item, null);
				holder.date = (TextView) view.findViewById(R.id.date);
				holder.desc = (TextView) view.findViewById(R.id.description);
				holder.amount = (TextView) view.findViewById(R.id.amount);
				holder.pos = position;
			}
			/**Else reuse the old one*/
		}else{
			holder = (ViewHolder) view.getTag();
		}

		/**Update the display values*/
		holder.date.setText(convertDate(detail.dates.formattedDate));
		holder.desc.setText(detail.description);
		final String amountString = BankStringFormatter.convertToDollars(detail.amount);
		holder.amount.setText(amountString);
		if(!amountString.contains(BankStringFormatter.NEGATIVE)){
			holder.amount.setTextColor(res.getColor(R.color.string_indicator));
		}
		view.setOnClickListener(getClickListener(holder.pos));
		view.setBackgroundResource((holder.pos%2 == 0) ? R.color.white : R.color.transaction_table_stripe);
		return view;
	}

	/**
	 * Get the amount of items being displayed total
	 * @return the amount of items being displayed total
	 */
	@Override
	public int getCount(){
		return details.size();
	}

	/**
	 * Get the item click listeners
	 * @param index - index of the item
	 * @return the item click listeners
	 */
	private OnClickListener getClickListener(final int index){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				fragment.goToDetailsScreen(index);
			}
		};
	}

	/**
	 * Convert the date from the format dd/MM/yyyy to dd/MM/yy
	 * @param date - date to be converted
	 * @return the converted date
	 */
	private String convertDate(final String date){
		final SimpleDateFormat serverFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);

		try{
			return tableFormat.format(serverFormat.parse(date));
		} catch (final ParseException e) {
			return date;
		}
	}

	/**
	 * Set the data in the adapter
	 * @param data - data to set in the adapter
	 */
	public void setData(final List<ActivityDetail> data){
		details = data;
	}

	/**
	 * Private class that holds view information
	 * @author jthornton
	 *
	 */
	private class ViewHolder {
		public TextView date;
		public TextView desc;
		public TextView amount;
		public int pos;
	}


}
