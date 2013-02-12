package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.discover.mobile.bank.R;

/**
 * ScrollView that will hold the not eligible for payments view.
 * This just holds static context.
 * 
 * @author jthornton
 *
 */
public class BankPayeeNotEligibleLayout extends ScrollView{

	/**
	 * View constructor for the class
	 * @param context - activity context
	 * @param attrs - attribute to apply
	 */
	public BankPayeeNotEligibleLayout(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		addView(LayoutInflater.from(context).inflate(R.layout.payee_no_eligible, null));
	}
}
