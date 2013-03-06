package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Class representation of the layout defined in res/select_list_item.xml that consists
 * of two labels stacked one on top of the other.
 * 
 * @author henryoyuela
 *
 */
public class SelectListItem extends RelativeLayout {
	protected TextView topLabel;
	protected TextView middleLabel;
	protected View view;


	public SelectListItem(final Context context) {
		super(context);
		doSetup(context);
	}
	
	public SelectListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public SelectListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}
	
	private RelativeLayout getInflatedLayout(final Context context) {
		return  (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.select_list_item, null);
	}
	
	private void doSetup(final Context context) {
		this.addView(getInflatedLayout(context));
		
		view = findViewById(R.id.list_item);
		topLabel = (TextView)view.findViewById(R.id.top_label);
		middleLabel = (TextView)view.findViewById(R.id.middle_label);
	}
	
	public void setTopLabelText(final String text) {
		topLabel.setText(text);
	}
	
	public void setMiddleLabelText(final String text) {
		middleLabel.setText(text);
	}
	
	
}
