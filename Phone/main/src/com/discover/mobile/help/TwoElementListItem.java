package com.discover.mobile.help;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
/**
 * This is a custom subclass of a RelativeLayout that acts as a list element that contains two labels.
 * It provides for an easy way to modify the appearance and function of this kind of list element.
 * 
 * @author scottseward
 *
 */
public class TwoElementListItem extends RelativeLayout{

	/**
	 * The two text view objects in this layout.
	 */
	private TextView leftTextView;
	private TextView rightTextView;
	private TextView addressTextView;
	
	//A horizontal gray line on the top of each view that acts as a divider.
	private View dividerLine;
	
	public TwoElementListItem(final Context context) {
		super(context);
		doSetup(context);
	}
	public TwoElementListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public TwoElementListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}

	/**
	 * Loads the relative layout for this view and adds its children to the layout.
	 * 
	 * @param context - the context/activity that created an instance of this layout.
	 */
	private void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		loadViews();
	}
	
	/**
	 * Returns the inflated layout for this list item.
	 * @param context the calling context.
	 * @return the inflated layout for this list item.
	 */
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.two_item_list_element, null);
	}
	
	/**
	 * Initialize the local text views with the objects from the layout.
	 */
	private void loadViews() {
		leftTextView = (TextView)findViewById(R.id.left_text);
		rightTextView = (TextView)findViewById(R.id.right_text);
		dividerLine = findViewById(R.id.divider_line);
		addressTextView = (TextView)findViewById(R.id.address_text);
	}
	
	/**
	 * Sets the text of the left text view to a given String value.
	 * @param leftText - a String to display in the left text view.
	 */
	public void setLeftText(final String leftText) {
		if(null == leftText){
			leftTextView.setText("");
		}else {
			leftTextView.setText(leftText);
		}
	}
	
	/**
	 * Sets the text of the right text view to a given String value.
	 * @param rightText - a String to display in the right text view.
	 */
	public void setRightText(final String rightText) {
		if(null == rightText){
			rightTextView.setText("");
		}else {
			rightTextView.setText(rightText);
		}
	}
	
	public void setAddressText(final String addressText){
		if(null == addressText){
			addressTextView.setText("");
		}else {
			addressTextView.setText(addressText);
		}
	}
	
	/**
	 * @return the leftTextView
	 */
	public TextView getLeftTextView() {
		return leftTextView;
	}
	/**
	 * @param leftTextView the leftTextView to set
	 */
	public void setLeftTextView(final TextView leftTextView) {
		this.leftTextView = leftTextView;
	}
	/**
	 * @return the rightTextView
	 */
	public TextView getRightTextView() {
		return rightTextView;
	}
	
	/**
	 * @return the addressTextView
	 */
	public TextView getAddressTextView() {
		return addressTextView;
	}
	/**
	 * @param rightTextView the rightTextView to set
	 */
	public void setRightTextView(final TextView rightTextView) {
		this.rightTextView = rightTextView;
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
