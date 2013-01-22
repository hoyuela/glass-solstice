package com.discover.mobile.views;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.R;

public class GeneralListItemAdapter extends ArrayAdapter<GeneralListItemModel> {
	
	private final LayoutInflater layoutInflater;
	
	public GeneralListItemAdapter(Context context, List<GeneralListItemModel> objects) {
		super(context, 0, objects);
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View cellView;
		
		if(convertView == null)
			cellView = layoutInflater.inflate(R.layout.general_list_item, null);
		else
			cellView = convertView;
		
		GeneralListItemModel model = getItem(position);
		
		setDefaultUIElements(cellView, model);
		setBottomBarUIElements(cellView, model);
		setBlueButtonUI(cellView, model);
		
		return cellView;
	}
	
	private static void setDefaultUIElements(View cellView, GeneralListItemModel model) {
		TextView titleView = (TextView) cellView.findViewById(R.id.title);
		titleView.setText(model.titleTextRes);
		TextView contentTextView = (TextView) cellView.findViewById(R.id.content_text);
		contentTextView.setText(model.contentTextRes);
	}
	
	private static void setBottomBarUIElements(View cellView, GeneralListItemModel model) {
		TextView bottomBarLabelTextView = (TextView) cellView.findViewById(R.id.bottom_bar_label);
		TextView bottomBarValueTextView = (TextView) cellView.findViewById(R.id.bottom_bar_value);
		
		if (model.bottomBarModel != null) {
			bottomBarLabelTextView.setText(model.bottomBarModel.labelTextRes);
			bottomBarValueTextView.setText(model.bottomBarModel.valueTextRes);
		} else {
			View generalListViewSeparatorLine = cellView.findViewById(R.id.general_list_item_separator_line);
			generalListViewSeparatorLine.setVisibility(View.INVISIBLE);
			bottomBarLabelTextView.setVisibility(View.GONE);
			bottomBarValueTextView.setVisibility(View.GONE);
		}
	}
	
	private static void setBlueButtonUI(View cellView, GeneralListItemModel model) {
		TextView blueButtonText = (TextView) cellView.findViewById(R.id.blue_button_text);
		
		if (model.actionButtonModel != null)
			blueButtonText.setText(model.actionButtonModel.buttonTextRes);
		else {
			View buttonDividerLine = cellView.findViewById(R.id.divider_line);
			buttonDividerLine.setVisibility(View.GONE);
			blueButtonText.setVisibility(View.GONE);
		}
	}
	
}
