package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

public class AccountToggleView extends RelativeLayout {

	private final Context context;
	private final View view;
	
	private final ImageView indicator;

	public AccountToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		LayoutInflater inflater;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.account_toggle, null);
		this.indicator = (ImageView) view.findViewById(R.id.acct_toggle_indicator);
	}

	/**
	 * Positions this view's triangular indicator below the bank or card icon.
	 * 
	 * @param icon
	 *            The view to which this should be placed below.
	 */
	public void positionIndicatorBelowIcon(ImageView icon) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		lp.addRule(RelativeLayout.ALIGN_RIGHT, icon.getId());
		lp.addRule(RelativeLayout.BELOW, icon.getId());
		indicator.setLayoutParams(lp);
	}

}
