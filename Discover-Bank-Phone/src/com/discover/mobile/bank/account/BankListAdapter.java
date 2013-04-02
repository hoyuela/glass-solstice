package com.discover.mobile.bank.account;

import java.text.NumberFormat;
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

	/**Integer value to convert from cents to dollar*/
	private static final int DOLLAR_CONVERSION = 100;

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
		ItemViewHolder holder = null;
		final ActivityDetail detail = details.get(position);

		/**If the view is null, create a new one*/
		if(null == view || !(view.getTag() instanceof ItemViewHolder)){
			holder = new ItemViewHolder();
			if(null != detail){
				view = inflater.inflate(R.layout.bank_table_item, null);
				holder.date = (TextView) view.findViewById(R.id.date);
				holder.desc = (TextView) view.findViewById(R.id.description);
				holder.amount = (TextView) view.findViewById(R.id.amount);
				holder.pos = position;
			}
			/**Else reuse the old one*/
		}else{
			holder = (ItemViewHolder) view.getTag();
		}

		/**Update the display values*/
		holder.date.setText(convertDate(detail.dates.get(ActivityDetail.POSTED).split(ActivityDetail.DATE_DIVIDER)[0]));
		holder.desc.setText(detail.description);
		final double amount = ((double)detail.amount.value)/DOLLAR_CONVERSION;
		if(amount == 0.00){
			holder.amount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amount));
		} else if(amount < 0){
			holder.amount.setText("-"+NumberFormat.getCurrencyInstance(Locale.US).format(amount*-1));
		}else{
			holder.amount.setTextColor(res.getColor(R.color.green_acceptance));
			holder.amount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amount));
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
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

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
	private class ItemViewHolder {
		public TextView date;
		public TextView desc;
		public TextView amount;
		public int pos;
	}
}
