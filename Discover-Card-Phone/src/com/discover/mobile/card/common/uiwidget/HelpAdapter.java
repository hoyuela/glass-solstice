/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.card.common.uiwidget;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.card.R;


/**
 * Help adapter used to show items in the search widget.  This adapter will
 * used a list of HelpItemGenerator objects to build views from the help_list_item.xml
 * file and display them in the list view of the help search bar.
 * 
 * @author jthornton
 *
 */
public class HelpAdapter extends ArrayAdapter<List<HelpItemGenerator>>{

	/**List of details to show*/
	private List<HelpItemGenerator> data;

	/**Inflater used to inflate layouts*/
	private final LayoutInflater inflater;

	/**
	 * Constructor for the adapter
	 * @param context - activity context
	 * @param textViewResourceId - resource id that needs to be shown
	 * @param data - data to be shown in the adapter (cannot be null)
	 */
	public HelpAdapter(final Context context, final int textViewResourceId, final List<HelpItemGenerator> data) {
		super(context, textViewResourceId);
		this.data = data;
		inflater = LayoutInflater.from(context);		
	}

	/**
	 * Create the view.  This includes setting the correct background and attaching
	 * the click listener to the view.
	 * @param postion - current position
	 * @param view - current view
	 * @param parent - parent view group
	 */
	@Override
	public View getView(final int position, View view, final ViewGroup parent){
		HelpViewHolder holder = null;

		final HelpItemGenerator detail = data.get(position);

		/**If the view is null, create a new one*/
		if(null == view || !(view.getTag() instanceof HelpViewHolder)){
			holder = new HelpViewHolder();
			if(null != detail){
				view = inflater.inflate(R.layout.card_help_list_item, null);
				holder.text = (TextView) view.findViewById(R.id.text);
			}
			/**Else reuse the old one*/
		}else{
			holder = (HelpViewHolder) view.getTag();
		}

		if(detail.isShowArrow()){
			final ImageSpan imagespan = 
					new ImageSpan(this.getContext(), R.drawable.detail_disclosure_white_arrow, ImageSpan.ALIGN_BASELINE); 
			final SpannableString text = new SpannableString(this.getContext().getString(detail.getText()) + "  ");
			text.setSpan(imagespan, text.length()-1, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			holder.text.setText(text);
		}else{
			holder.text.setText(detail.getText());
		}
		view.setBackgroundDrawable(this.getContext().getResources().getDrawable(getDrawable(detail.isDark(), position)));
		view.setOnClickListener(detail.getListener());
		return view;
	}

	/**
	 * Get the background resource
	 * @param isDark - if the resource should be dark
	 * @param position - position of the view
	 */
	public int getDrawable(final boolean isDark, final int position){
		if(data.size() == 1){
			return (isDark) ? R.drawable.help_dark_single : R.drawable.help_light_single;
		}else if(position == 0){
			return (isDark) ? R.drawable.help_dark_top : R.drawable.help_light_top;
		}else if(position == (data.size()-1)){
			return (isDark) ? R.drawable.help_dark_bottom : R.drawable.help_light_bottom;
		}else{
			return (isDark) ? R.drawable.help_dark_middle : R.drawable.help_light_middle;
		}
	}

	/**
	 * Get the amount of items being displayed total
	 * @return the amount of items being displayed total
	 */
	@Override
	public int getCount(){
		return data.size();
	}

	/**
	 * Set the data in the adapter
	 * @param data - data to set in the adapter
	 */
	public void setData(final List<HelpItemGenerator> data){
		this.data = data;
	}

	/**
	 * Private class that holds view information
	 * @author jthornton
	 *
	 */
	private class HelpViewHolder {
		public TextView text;
	}
}
