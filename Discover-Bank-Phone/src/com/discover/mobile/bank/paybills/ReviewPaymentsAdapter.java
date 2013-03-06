package com.discover.mobile.bank.paybills;

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

import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;

/**
 * Adapter for the review payments table.  Used to display each item as it comes on the screen.
 * @author jthornton
 *
 */
public class ReviewPaymentsAdapter  extends ArrayAdapter<List<PaymentDetail>>{

	/**List of details to show*/
	private List<PaymentDetail> details;

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	/**Resources of the application*/
	private final Resources res;

	/**Fragment currently displayed*/
	private final ReviewPaymentsTable fragment;

	/**Integer value to convert from cents to dollar*/
	private static final int DOLLAR_CONVERSION = 100;

	/**List of payees in the application*/
	private final ListPayeeDetail payees;

	/**
	 * Constuctor for the adapter
	 * @param context - activity context
	 * @param textViewResourceId - resource
	 * @param items - items to set in the adapter
	 * @param fragment - fragment using the adapter
	 */
	public ReviewPaymentsAdapter(final Context context, final int textViewResourceId, final ReviewPaymentsTable fragment) {
		super(context, textViewResourceId);
		inflater = LayoutInflater.from(context);
		res = context.getResources();
		this.fragment =fragment;
		payees = BankUser.instance().getPayees();
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

		/**Show the header if is is at the top of the list*/
		if (position == 0){
			view = fragment.getHeader();
			return view;
		}
		/**If the details is empty show the message*/
		if(details.isEmpty()){
			fragment.showFooterMessage();
			view = fragment.getFooter();
			return view;
		}

		/**At the end of the list try loading more*/
		if(position == details.size() + 1){
			view = fragment.getFooter();
			return view;
		}

		final PaymentDetail detail = details.get(position-1);

		/**If the view is null, create a new one*/
		if(null == view || !(view.getTag() instanceof ViewHolder)){
			holder = new ViewHolder();
			if(null != detail){
				view = inflater.inflate(R.layout.bank_table_item, null);
				holder.date = (TextView) view.findViewById(R.id.date);
				holder.payee = (TextView) view.findViewById(R.id.description);
				holder.amount = (TextView) view.findViewById(R.id.amount);
				holder.pos = position;
			}
			/**Else reuse the old one*/
		}else{
			holder = (ViewHolder) view.getTag();
		}

		/**Update the display values*/
		holder.date.setText(convertDate(detail));
		holder.payee.setText(payees.getNameFromId(detail.payee.id));
		final double amount = ((double)detail.amount.value)/DOLLAR_CONVERSION;
		if(amount < 0){
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
		return details.size()+2;
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
	private String convertDate(final PaymentDetail item){
		final String itemStatus = item.status;
		String date = "";
		if("SCHEDULED".equals(itemStatus)){
			date = item.deliverBy.split(PaymentDetail.DATE_DIVIDER)[0];
		}else if("PAID".equals(itemStatus)){
			date = item.deliverBy.split(PaymentDetail.DATE_DIVIDER)[0];
		}else{
			date = item.deliverBy.split(PaymentDetail.DATE_DIVIDER)[0];
		}
		return convertDate(date);
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
			final String dateString = date.split(PaymentDetail.DATE_DIVIDER)[0];
			return tableFormat.format(serverFormat.parse(dateString));
		} catch (final ParseException e) {
			return date;
		}
	}

	/**
	 * Set the data in the adapter
	 * @param data - data to set in the adapter
	 */
	public void setData(final List<PaymentDetail> data){
		details = data;
	}

	/**
	 * Private class that holds view information
	 * @author jthornton
	 *
	 */
	private class ViewHolder {
		public TextView date;
		public TextView payee;
		public TextView amount;
		public int pos;
	}
}
