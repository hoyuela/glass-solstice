package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.discover.mobile.R;

/**
 * Temp class stubbing out terms and conditions for payments
 * @author jthornton
 *
 */
public class BankPayTerms extends ScrollView{

	public BankPayTerms(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		addView(LayoutInflater.from(context).inflate(R.layout.payment_terms_and_conditions, null));
	}

}
