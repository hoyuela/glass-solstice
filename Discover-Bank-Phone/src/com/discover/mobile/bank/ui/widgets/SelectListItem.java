package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
	protected View divider;
	protected ImageView image;
	protected RelativeLayout mainLayout;


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
		image = (ImageView)view.findViewById(R.id.caret);
		divider = findViewById(R.id.divider_line);
		mainLayout = (RelativeLayout)view.findViewById(R.id.list_item);
	}
	
	public void setTopLabelText(final String text) {
		topLabel.setText(text);
	}
	
	public void setMiddleLabelText(final String text) {
		middleLabel.setText(text);
	}
	
	/**
	 * 
	 * @return Returns returns the TextView with the id top_label within this view.
	 */
	public TextView getTopLabel() {
		return topLabel;
	}
	
	public RelativeLayout getMainLayout() {
		return mainLayout;
	}
	
	/**
	 * 
	 * @return Returns returns the TextView with the id middle_lable within this view.
	 */
	public TextView getMiddleLabel(){
		return middleLabel;
	}
	
	public void showMiddleLabel(final boolean show) {
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
			        RelativeLayout.LayoutParams.WRAP_CONTENT);
		if( show ) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			topLabel.setLayoutParams(params);
			middleLabel.setVisibility(View.VISIBLE);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			topLabel.setLayoutParams(params);
		
			middleLabel.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Method used to hide image on the side of the view.
	 * 
	 * @param show True to show image, false otherwise.
	 */
	public void showImage(final boolean show) {
		if( show ) {
			image.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.INVISIBLE);
		}
	}
	
	public void showDivider(final boolean show) {
		if( show ) {
			divider.setVisibility(View.VISIBLE);
		} else {
			divider.setVisibility(View.INVISIBLE);
		}
	}
	
	
}
