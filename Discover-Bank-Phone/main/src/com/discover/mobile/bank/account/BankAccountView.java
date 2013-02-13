package com.discover.mobile.bank.account;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountNumber;
import com.discover.mobile.bank.services.account.Money;
import com.google.common.base.Strings;

/**
 * Widget used to display an Account object. Uses the layout specified in res\layout\bank_account_view.xml to display
 * the data.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountView extends RelativeLayout implements OnClickListener {
	/**
	 * TAG used to print logs into Android logcat
	 */
	private final static String TAG = BankAccountView.class.getSimpleName();
	
	/**
	 * Used to display the nickname field value in an Account object
	 */
	private final TextView acctNickName;
	/**
	 * Used to display the balance field value in an Account object
	 */
	private final TextView acctBalance;
	/**
	 * Used to display the account ending number value in an Account object
	 */
	private final TextView acctEnding;
	/**
	 * Holds a reference to the view that displays a carat image
	 */
	private final View carat;
	/**
	 * Holds a reference to the Account object whose data is displayed in the view
	 */
	private Account account;
	/**
	 * Reference to layout used for this view
	 */
	private final RelativeLayout layout;

	/**
	 * 
	 * @param context Reference to Activity that hosts the view
	 */
	public BankAccountView(final Context context) {
		super(context);
		
		layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.bank_account_view, null);
		
		acctNickName = (TextView)layout.findViewById(R.id.acct_nickname);
		acctBalance = (TextView)layout.findViewById(R.id.acct_balance);
		acctEnding = (TextView)layout.findViewById(R.id.acct_ending);
		carat = layout.findViewById(R.id.acct_carat);
		
		addView(layout);
	}
	
	/**
	 * 
	 * @param context Reference to Activity that hosts the view
	 * @param account Reference to account object whose data will be displayed in this view
	 */
	public BankAccountView(final Context context, final Account account) {
		this(context);
		
		this.setAccountInformation(account);
		
		this.account = account;
	}
	
	/**
	 * 
	 * @param value Reference to a string that holds an Account's nickname.
	 */
	public void setNickName(final String value) {
		this.acctNickName.setText(value);
	}
	
	/**
	 * 
	 * @param value Reference to a string that holds an Account's balance.
	 */
	public void setBalance(final Money value) {
		if( !Strings.isNullOrEmpty(value.formatted)) {
			try{
				/**Set Text view for displaying balance for account */
				this.acctBalance.setText(value.formatted);
				
				/**Set color of text to red if negative balance otherwise black*/
				final int color = (value.formatted.charAt(0) == '-') ? R.color.error_indicator : R.color.black;
				this.acctBalance.setTextColor(getResources().getColor(color));
				
			}catch(final Exception ex) {
				this.acctBalance.setText(R.string.acct_total_str);
			}
		} else {
			this.acctBalance.setText(R.string.acct_total_str);
		}
		
	}
	
	/**
	 * 
	 * @param value Reference to a string that holds an Account's ending.
	 */
	public void setEnding(final AccountNumber value) {
		try {
			this.acctEnding.setText(BankStringFormatter.convertToAccountEnding(value.ending));
		} catch(final Exception ex) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				this.acctEnding.setText("");
			}
		}
	}

	/**
	 * 
	 * @param account Reference to an Account object that holds all the information to display in this view.
	 */
	public void setAccountInformation(final Account account) {
		
		this.setEnding(account.accountNumber);
		this.setBalance(account.balance);
		this.setNickName(Strings.nullToEmpty(account.nickname));
		
		/**User should only be allowed to navigate to account details if it is non-IRA account*/
		if( account.type.equalsIgnoreCase(Account.ACCOUNT_IRA)) {
			this.carat.setVisibility(INVISIBLE);
		} else {
			layout.setOnClickListener(this);
			this.carat.setVisibility(VISIBLE);
		}
	}

	/**
	 * Sends a service call to download the posted transaction details for the account associated with this view.
	 */
	@Override
	public void onClick(final View v) {
		BankNavigator.navigateToAccountDetails(account);
	}
}
