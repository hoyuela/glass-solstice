package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;

/**
 * Custom view for displaying the header navigation. This is used to display to
 * the user what position they are at in the process.
 * 
 * @author ajleeds
 * 
 */
public abstract class HeaderProgressIndicator extends RelativeLayout {

	protected TextView step1;
	protected TextView step2;
	protected TextView step3;
	protected ImageView step1Confirm;
	protected ImageView step2Confirm;
	protected ImageView indicator1;
	protected ImageView indicator2;
	protected ImageView indicator3;
	
	protected abstract void inflateHeader();
	public abstract void setPosition(int position);
	public abstract void setTitle(int str1, int str2, int str3);
	
	public HeaderProgressIndicator(final Context context) {
		super(context);
	}

	public HeaderProgressIndicator(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderProgressIndicator(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Initiates the header and sets the current position for Change Password
	 * @param position - Current position between 0-2
	 */
	public void initChangePasswordHeader(final int position) {
		inflateHeader();
		setTitle(R.string.enter_info, R.string.create_password,R.string.confirm);
		setPosition(position);
	}
}
