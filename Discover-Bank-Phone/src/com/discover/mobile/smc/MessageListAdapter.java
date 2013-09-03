package com.discover.mobile.smc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.R.color;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Adapter used to show the list of messages
 * @author juliandale
 *
 */
public class MessageListAdapter extends ArrayAdapter<List<MessageListItem>>{

	/**data list used to pull message details from*/
	private List<MessageListItem> data;
	/**inflater used to create the list_item layout*/
	private LayoutInflater inflater; 
	/**reference to resource file to obtain colors for list items*/
	private Resources res;
	public MessageListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.data = new ArrayList<MessageListItem>();
		inflater = LayoutInflater.from(context);
		this.res = context.getResources();
	}

	/**
	 * must overide this in order for getview to be called.
	 * Returns the data size + 1.  This is used to trick the 
	 * adapter into adding one more element.  The added element is the 
	 * disclaimer at the bottom of the list.
	 */
	@Override
	public int getCount(){
		if(null == data) {
			return 0;
		} else if(data.size() > 0){
			//if there is at least one item in the 
			//list return size + 1
			return data.size() + 1;
		} else {
			//data size is zero, return it.
			return data.size();
		}
	}
	
	/**
	 * Allows user to swtich the data in the list.
	 * Used for switching between inbox and sent box
	 * @param data
	 */
	public void setData(List<MessageListItem> data){
		this.data = data;
	}
	
	@Override
	public View getView(final int position, View view, final ViewGroup parent){
		//check to see if were are out side of the index range for the data.
		//if so, return a text view with the disclaimner.
		if(position == data.size()) {
			TextView disclaimer = new TextView(DiscoverActivityManager.getActiveActivity());
			disclaimer.setText(res.getString(R.string.smc_disclaimer));
			disclaimer.setGravity(Gravity.CENTER_HORIZONTAL);
			return disclaimer;
		}
		
		ItemViewHolder holder = null;
		//retrieve the message details for this list item
		final MessageListItem item = data.get(position);
		//check to see if the view is being recylced, if not create
		//a new view and view holder
		if(null == view || !(view.getTag() instanceof ItemViewHolder)){
			view = inflater.inflate(R.layout.bank_smc_list_item, null);
			holder = new ItemViewHolder();
			holder.titleView = (TextView)view.findViewById(R.id.message_title);
			holder.accountView = (TextView)view.findViewById(R.id.message_account);
			holder.dateView = (TextView)view.findViewById(R.id.message_date);
		} else {
			//retrieve holder from recycled view
			holder  = (ItemViewHolder) view.getTag();
		}
		//set the fields
		holder.titleView.setText(item.subject);
		holder.accountView.setText(item.account.nickname);
		holder.dateView.setText(convertDate(item.messageDate));
		//update the background depending on if the message was read or not
		if(!item.readMessageStatus.equals(MessageListItem.OPENED)){
			view.setBackgroundColor(res.getColor(R.color.white));
			holder.titleView.setTypeface(null, Typeface.BOLD);
		} else {
			view.setBackgroundResource(R.drawable.common_table_list_item_gray);
			holder.titleView.setTypeface(Typeface.DEFAULT);
		}
		//set the tag 
		view.setTag(holder);
		return view;
	}
	
	/**
	 * Convert the server date (UTC) to simple dd/MM format
	 * @param date
	 * @return
	 */
	private String convertDate(final String date) {
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		final SimpleDateFormat listFormat = new SimpleDateFormat("dd/MM", Locale.US);
		try {
			return listFormat.format(serverFormat.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	/**
	 * Prive inclass that holder references to ui elements
	 * for each list item.
	 * @author juliandale
	 *
	 */
	private static class ItemViewHolder {
		 private TextView titleView;
		 private TextView accountView;
		 private TextView dateView;
	}
}
