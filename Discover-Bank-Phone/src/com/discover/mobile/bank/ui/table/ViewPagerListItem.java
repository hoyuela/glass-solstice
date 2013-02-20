package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * A list item to show inside of a ViewPager detail item.
 * Contains 3 text labels and a divider line.
 * 
 * @author scottseward
 *
 */
public class ViewPagerListItem extends RelativeLayout {
	
	private TextView topLabel;
	private TextView middleLabel;
	private TextView bottomLabel;
	private View dividerLine;
	
	public ViewPagerListItem(final Context context) {
		super(context);
		doSetup(context);
	}
	public ViewPagerListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public ViewPagerListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}

	private void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		loadViews();
	}
	
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.view_pager_list_item, null);
	}
	
	private void loadViews() {
		topLabel = (TextView)findViewById(R.id.top_label);
		middleLabel = (TextView)findViewById(R.id.middle_label);
		bottomLabel = (TextView)findViewById(R.id.bottom_label);
		dividerLine = findViewById(R.id.divider_line);
	}
	
	/**
	 * @return the topLabel
	 */
	public TextView getTopLabel() {
		return topLabel;
	}
	/**
	 * @param topLabel the topLabel to set
	 */
	public void setTopLabel(final TextView topLabel) {
		this.topLabel = topLabel;
	}
	/**
	 * @return the middleLabel
	 */
	public TextView getMiddleLabel() {
		return middleLabel;
	}
	/**
	 * @param middleLabel the middleLabel to set
	 */
	public void setMiddleLabel(final TextView middleLabel) {
		this.middleLabel = middleLabel;
	}
	/**
	 * @return the bottomLabel
	 */
	public TextView getBottomLabel() {
		return bottomLabel;
	}
	/**
	 * @param bottomLabel the bottomLabel to set
	 */
	public void setBottomLabel(final TextView bottomLabel) {
		this.bottomLabel = bottomLabel;
	}
	/**
	 * @return the dividerLine
	 */
	public View getDividerLine() {
		return dividerLine;
	}
	/**
	 * @param dividerLine the dividerLine to set
	 */
	public void setDividerLine(final View dividerLine) {
		this.dividerLine = dividerLine;
	}
}
