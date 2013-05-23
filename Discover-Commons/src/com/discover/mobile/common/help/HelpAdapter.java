/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.common.help;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.common.R;

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

	public List<HelpItemGenerator> getData() {
		return data;
	}

	/** Reference to context hosting adapter */
	private final Context context;


	/**
	 * Constructor for the adapter
	 * @param context - activity context
	 * @param textViewResourceId - resource id that needs to be shown
	 * @param data - data to be shown in the adapter (cannot be null)
	 */
	public HelpAdapter(final Context context, final int textViewResourceId, final List<HelpItemGenerator> data) {
		super(context, textViewResourceId);
		this.data = data;
		this.context = context;
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
		/** If the view is null, create a new one */
		if (null == view) {
			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.common_help_list_item, null);
		}

		final HelpItemGenerator detail = data.get(position);

		if (detail != null) {
			final TextView menuItem = (TextView) view.findViewById(R.id.text);

			menuItem.setBackgroundDrawable(context.getResources().getDrawable(getDrawable(detail.isDark(), position)));
			menuItem.setText(detail.getText());

			if (!detail.isShowArrow()) {
				menuItem.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			} else {
				/**
				 * Calculate padding necessary to draw the caret on the right of
				 * text correctly this can only be done after the view has been
				 * drawn
				 */
				final ViewTreeObserver viewTreeObserver = menuItem.getViewTreeObserver();
				if (viewTreeObserver.isAlive()) {
					viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							menuItem.getViewTreeObserver().removeGlobalOnLayoutListener(this);

							final String text = menuItem.getText().toString();
							final int rightPadding = (int) (menuItem.getMeasuredWidth() - menuItem.getPaint().measureText(text) - 30);
							menuItem.setPadding(menuItem.getPaddingLeft(), menuItem.getPaddingTop(), rightPadding, menuItem.getPaddingBottom());
						}
					});
				}
			}

			view.setOnClickListener(detail.getListener());
		}

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
}