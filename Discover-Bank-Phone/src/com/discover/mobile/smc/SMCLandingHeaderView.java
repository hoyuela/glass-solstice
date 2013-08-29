package com.discover.mobile.smc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.discover.mobile.bank.R;

/**
 * Contains the header view for the inbox landing page.
 * @author juliandale
 *
 */
public class SMCLandingHeaderView extends RelativeLayout {
	/*
	 * Header button view that will be inflated
	 */
	private final View view;
	
	public SMCLandingHeaderView(Context context, final AttributeSet attrs) {
		super(context,attrs);
		view = LayoutInflater.from(context).inflate(R.layout.bank_smc_header, null);
		addView(view);
	}

}
