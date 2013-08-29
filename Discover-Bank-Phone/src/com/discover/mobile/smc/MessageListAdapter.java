package com.discover.mobile.smc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.R.color;

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
	
	public MessageListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.data = new ArrayList<MessageListItem>();
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount(){
		if(null == data) return 0;
		return data.size();
	}
	
	public void setData(List<MessageListItem> data){
		this.data = data;
	}
	
	@Override
	public View getView(final int position, View view, final ViewGroup parent){
		ItemViewHolder holder = null;
		final MessageListItem item = data.get(position);
		if(null == view || !(view.getTag() instanceof ItemViewHolder)){
			view = inflater.inflate(R.layout.bank_smc_list_item, null);
			holder = new ItemViewHolder();
			holder.titleView = (TextView)view.findViewById(R.id.message_title);
			holder.accountView = (TextView)view.findViewById(R.id.message_account);
			holder.dateView = (TextView)view.findViewById(R.id.message_date);
		} else {
			holder  = (ItemViewHolder) view.getTag();
		}
		//set the fields
		holder.titleView.setText(item.subject);
		holder.accountView.setText(item.account.nickname);
		holder.dateView.setText(convertDate(item.messageDate));
		//update the background depending on if the message was read or not
		if(!item.readMessageStatus.equals("opened")){
			view.setBackgroundColor(Color.WHITE);
		} else {
			view.setBackgroundColor(Color.GRAY);
		}
		//set the tag 
		view.setTag(holder);
		return view;
	}
	
	
	private String convertDate(final String date) {
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		final SimpleDateFormat listFormat = new SimpleDateFormat("dd/MM", Locale.US);
		try {
			return listFormat.format(serverFormat.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	private static class ItemViewHolder {
		 private TextView titleView;
		 private TextView accountView;
		 private TextView dateView;
	}
}
