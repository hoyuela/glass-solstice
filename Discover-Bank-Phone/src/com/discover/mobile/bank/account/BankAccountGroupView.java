package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.net.json.bank.Money;

/**
 * Widget used to display a user's Account information in a Grouped format. This view will display each account in the group within a BankAccountView.
 * Uses the layout specified in res\layout\bank_account_group_view.xml to display the data.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountGroupView extends LinearLayout  {
	/**
	 * TAG used to print logs into Android logcat
	 */
	private static final String TAG = BankAccountGroupView.class.getSimpleName();
	/**
	 * TextView used to display the type accounts that are associated with this view
	 */
	private final TextView type;
	/**
	 * TextView used to display the sum total of the balance in each account that is associated with this view
	 */
	private final TextView total;
	/**
	 * Reference to list of accounts associated with this view
	 */
	private final List<Account> acctList;
	/**
	 * Reference to layout used to display content for this view
	 */
	final LinearLayout layout;
	
	/** Reference to the last {@code BankAccountView} that belongs to this view. */
	private BankAccountView lastAccountReference;
	/** Reference to the first {@code BankAccountView} that belongs to this view. */
	private BankAccountView firstAccountReference;
	
	public BankAccountGroupView(final Context context) {
		super(context);
		
		layout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.bank_account_group_view, null);
		
		type = (TextView)layout.findViewById(R.id.acct_type);
		total = (TextView) layout.findViewById(R.id.acct_all_total);
		acctList = new ArrayList<Account>();
		lastAccountReference = null;
		firstAccountReference = null;
		
		addView(layout);
		
	}
	/**
	 * 	Method used to add an account's balance to the group's total.
	 * 
	 * @param value String representation of an account's balance
	 */
	private void addToBlance(final Money value) {
		double currentValue = 0;
		
		try {
			//Remove any '$' or ',' from the value
			final String numStrValue = value.formatted.replaceAll("[$,]+", "");
			//Remove any '$' or ',' from the current running value
			final String numStrTotalValue = total.getText().toString().replaceAll("[$,]+", "");
			//Convert string representation of sum total to a double to be able to add it
			currentValue = Double.parseDouble(numStrTotalValue);
			//Convert string representation of the incoming balance and add to current sum total
			currentValue += Double.parseDouble(numStrValue);
			//Set newly calculated balance in the view
			setBalance(currentValue);
		} catch( final Exception ex) {
			if(Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to add amount to group balance");
			}
		}
	}
	/**
	 * Method used to update the Account Group Total Sum displayed in the view.
	 * 
	 * @param value New Group total balance
	 */
	private void setBalance(final double value) {
		//Format incoming value to a String with a $ prefix
		final String text = BankStringFormatter.convertToDollars(Double.toString(value));
		//If balance value is negative change color to red otherwise set as black
		final int color = (value < 0) ? R.color.error_indicator : R.color.black;
		
		//Change color of text based on balance total
		total.setTextColor(getResources().getColor(color));
		
		//Set total after formatting
		total.setText(text);
	}
	/**
	 * Method used to show or hide Group Total Sum
	 * 
	 * @param value True to show group total balance, false otherwise
	 */
	private void showBalance( final boolean value ) {
		if(value) {
			total.setVisibility(VISIBLE);
		} else {
			total.setVisibility(GONE);
		}	
	}
	/**
	 * Method used to associate an account with a group.
	 * 
	 * @param account Reference to an account object 
	 */
	public void addAccount(final Account account) {
		if( null != account ) {
			if( acctList.size() == 0 || acctList.get(0).type.equals(account.type)) {
				/**Use name for grouping otherwise use type*/
				final String groupName = (Strings.isNullOrEmpty(account.name)) ? account.type : account.name;
				
				if(acctList.size() == 0 ) {
					//Set the name for the group
					type.setText(groupName);
				} else {
					if( !groupName.endsWith("s")) {
						//Set the name for the group with s at the end if more than one
						type.setText(groupName +"s");
					}
				}
				
				
				layout.addView(lastAccountReference = new BankAccountView(this.getContext(), account));
				
				if(firstAccountReference == null) {
					firstAccountReference = lastAccountReference;
				}
				
				
				this.addToBlance(account.balance);
	
				this.acctList.add(account);
						
				//Show Balance only if more than one account in group
				showBalance(acctList.size() > 1);
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to add account to group [Invalid Type]");
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to add account to group [Null]");
			}
			return;
		}
	}
	
	/**
	 * Method used to fetch the title for the group based on account type
	 * 
	 * @param account Reference to Account object whose type will be used to determien the title
	 * 
	 * @return Return a Resource Identifier for a string to use as a title
	 */
	public int getGroupTitle( final Account account ) {
		int ret = 0;
		
		//Group for Checking: Holds only Checking Types
		if( account.type.equals(Account.ACCOUNT_CHECKING)) {		
			ret = R.string.bank_checking_account;	
		}
		//Group for Savings: Holds Online Savings, MMA, CDs
		else if( account.type.equals(Account.ACCOUNT_SAVINGS) || 
				 account.type.equals(Account.ACCOUNT_MMA) ||
				 account.type.equals(Account.ACCOUNT_CD)) {
			ret = R.string.bank_savings_account;
			
		}
		//Group for Retirement Plans: Holds IRA, IRA CDs
		else if( account.type.equals(Account.ACCOUNT_IRA)) {
			ret = R.string.bank_ira_account;
		}
		//Group Personal Loans: Personal Loans
		else if( account.type.equals(Account.ACCOUNT_LOAN)) {
			ret = R.string.bank_loan_account;
		} else {
			ret = R.string.bank_unknown_account;
		}
		
		return ret;
	}
	
	/**
	 * Returns the number of {@code BankAccountView} belonging to this group.
	 * 
	 * @return number of children in group.
	 */
	public int getGroupSize() {
		return acctList.size();
	}

	/**
	 * Draws a solid stroke (not dashed) on the bottom of the last element of
	 * the group.
	 * 
	 * @param context
	 */
	public void addBottomStroke(Context context) {
		lastAccountReference.drawBottomStroke(context);
	}

	/**
	 * Draws a solid stroke (not dashed) on the top of the first element of the
	 * group.
	 * 
	 * @param context
	 */
	public void addTopStroke(Context context) {
		firstAccountReference.drawTopStroke(context);
	}

	/**
	 * Draws a solid stroke (not dashed) on all sides of the first element of
	 * the group.
	 * 
	 * @param context
	 */
	public void addAllStrokes(Context context) {
		firstAccountReference.drawAllStrokes(context);
	}
}
