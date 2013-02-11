package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.bank.account.Account;

public class BankAccountGroupView extends LinearLayout {
	private final TextView type;
	private final TextView total;
	private final List<Account> acctList;
	final LinearLayout layout;
	
	public BankAccountGroupView(final Context context) {
		super(context);
		
		layout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.bank_account_group_view, null);
		
		type = (TextView)layout.findViewById(R.id.acct_type);
		total = (TextView) layout.findViewById(R.id.acct_all_total);
		acctList = new ArrayList<Account>();
		
		addView(layout);
		
	}
		
	private void addToBlance(final String value) {
		int currentValue = 0;
		
		try {
			currentValue = Integer.parseInt(total.getText().toString());
			currentValue += Integer.parseInt(value);
			setBalance(currentValue);
		} catch( final NumberFormatException ex) {
			
		}
	}
	
	private void setBalance(final int value) {
		final String text = BankStringFormatter.convertToDollars(Integer.toString(value));
		final int color = (value < 0) ? R.color.error_indicator : R.color.body_copy;
		
		//Change color of text based on balance total
		total.setTextColor(color);
		
		//Set total after formatting
		total.setText(text);
	}
	
	private void showBalance( final boolean value ) {
		if(value) {
			total.setVisibility(VISIBLE);
		} else {
			total.setVisibility(GONE);
		}	
	}
		
	public void addAccount(final Account account) {
		if( null != account ) {
			if( acctList.size() == 0 || acctList.get(0).type == account.type) {
				//Set the account type for the group
				type.setText(account.name);
				
				layout.addView(new BankAccountView(this.getContext(), account));
				
				this.addToBlance(account.balance);
	
				this.acctList.add(account);
						
				//Show Balance only if more than one account in group
				showBalance(acctList.size() > 1);
			} else {
				//TODO: Log Error
			}
		} else {
			//TODO: Log Error
			return;
		}
	}

}
