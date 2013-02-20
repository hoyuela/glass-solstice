package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.PayeeDetail;

/**
 * BankManagePayee
 * A subclass of BankSelectPayee.
 * Essentially the same as BankSelectPayee with some slight modifications to make it a MangePayee screen.
 * 
 * @author scottseward
 *
 */
public class BankManagePayee extends BankSelectPayee{
	private int index = 0;

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.manage_payees;
	}
	
	/**
	 * For the manage payees screen we need to change the title in the action bar,
	 * hide the feedback button, and then display a message if the user has no payees.
	 */
	@Override
	protected void customSetup() {
		final TextView title = (TextView)getView().findViewById(R.id.select_payee_title);
		final TextView feedbackButton = (TextView)getView().findViewById(R.id.provide_feedback_button);
		feedbackButton.setVisibility(View.GONE);
		if(getPayees().payees == null || getPayees().payees.size() < 1){
			title.setText(R.string.manage_payees_welcome);
		}else{
			title.setText(R.string.empty);
		}
		//Reset the index so that if this screen gets re-created or re-used
		//the index is not in the wrong position.
		index = 0;
	}
	
	/**
	 * Returns an OnClickListener for a list item so that when the item is pressed, its index
	 * is known and we can navigate to the details screen for that item.
	 */
	@Override
	protected OnClickListener getOnClickListener(final PayeeDetail details) {
		
		return new OnClickListener() {
			private final int localIndex = getIndex();
			
			@Override
			public void onClick(final View v) {
				
				final Bundle bundle = new Bundle();
				bundle.putSerializable(BankExtraKeys.PAYEES_LIST, getPayees());
				bundle.putInt(BankExtraKeys.SELECTED_PAYEE, localIndex);
				BankNavigator.navigateToPayeeDetailScreen(bundle);
			}
		};
	}
	
	/**
	 * Returns an index value to be used for the list items.
	 * Returns 0 the first time, then 1 then 2 etc...
	 * @return the current value of index.
	 */
	private int getIndex() {
		return index++;
	}
	
}
