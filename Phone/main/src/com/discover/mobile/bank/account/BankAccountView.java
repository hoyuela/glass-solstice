package com.discover.mobile.bank.account;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.bank.account.Account;
import com.google.common.base.Strings;

public class BankAccountView extends RelativeLayout {
	
	final TextView acctNickName;
	final TextView acctBalance;
	final TextView acctEnding;
	final View carat;

	/**
	 * 
	 * @param context
	 */
	public BankAccountView(final Context context) {
		super(context);
		
		final RelativeLayout layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.bank_account_view, null);
		
		acctNickName = (TextView)layout.findViewById(R.id.acct_nickname);
		acctBalance = (TextView)layout.findViewById(R.id.acct_balance);
		acctEnding = (TextView)layout.findViewById(R.id.acct_ending);
		carat = layout.findViewById(R.id.acct_carat);
		
		addView(layout);
	}
	
	public BankAccountView(final Context context, final Account account) {
		this(context);
		
		this.setAccountInformation(account);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setNickName(final String value) {
		//TODO: Format String
		this.acctNickName.setText(value);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setBalance(final String value) {
		if( !Strings.isNullOrEmpty(value)) {
			try{
				this.acctBalance.setText(BankStringFormatter.convertToDollars(value));
			}catch(final Exception ex) {
				this.acctBalance.setText(R.string.acct_total_str);
			}
		} else {
			this.acctBalance.setText(R.string.acct_total_str);
		}
		
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setEnding(final String value) {
		try {
			this.acctEnding.setText(BankStringFormatter.convertToAccountEnding(value));
		} catch(final Exception ex) {
			
		}
	}

	/**
	 * 
	 * @param account
	 */
	public void setAccountInformation(final Account account) {
		this.setEnding(account.ending);
		this.setBalance(account.balance);
		this.setNickName(account.nickname);
		
		if( account.type == Account.ACCOUNT_IRA ) {
			this.carat.setVisibility(GONE);
		} else {
			this.carat.setVisibility(VISIBLE);
		}
	}
}
