package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.discover.mobile.R;
import com.discover.mobile.bank.BankServiceCallFactory;

/**
 * Temp class stubbing out terms and conditions for payments
 * @author jthornton
 *
 */
public class BankSelectPayee extends ScrollView{

	public BankSelectPayee(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		addView(LayoutInflater.from(context).inflate(R.layout.select_payee, null));
	}


	public void getPayees(){
		BankServiceCallFactory.createGetPayeeServiceRequest().submit();
	}

}