package com.discover.mobile.bank.payees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.paybills.BankSelectPayee;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;

/**
 * BankManagePayee
 * A subclass of BankSelectPayee.
 * Essentially the same as BankSelectPayee with some slight modifications to make it a MangePayee screen.
 * 
 * @author scottseward
 *
 */
public class BankManagePayee extends BankSelectPayee implements OnClickListener{
	private static final int DURATION = 5000;
	private int index = 0;
	/**
	 * Reference to button that will open the step 2 in the Add Payee work flow
	 */
	private Button addPayee;
	/**
	 * Reference to widget displayed when a payee is deleted from list
	 */
	private StatusMessageView statusView;
	
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Set String for when user does not have payees and has navigated to this screen*/
		getEmpty().setText(R.string.bank_payee_empty);
		
		/**Set click listener to open BankEnterPayeeFragment*/
		addPayee = (Button)view.findViewById(R.id.add_payee);
		addPayee.setOnClickListener(this);
		
		/**Set Text to Add New */
		addPayee.setText(R.string.select_payee_add_new);
		
		/**Set status message used for delete payee confirmation*/
		statusView = (StatusMessageView) view.findViewById(R.id.status);
		
		return view;
	}
	
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
		if(getPayees().payees == null || getPayees().payees.size() < 1){
			title.setText(R.string.manage_payees_welcome);
		}else{
			/**Adjust height so there is not a large gap between list and help icon*/
			title.setText(R.string.empty);
			
			final LinearLayout layoutParams = (LinearLayout)this.getView().findViewById(R.id.page_header);
			final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layoutParams.getLayoutParams();
			params.bottomMargin = 0;
			layoutParams.setLayoutParams(params);
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
				BankConductor.navigateToPayeeDetailScreen(bundle);
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


	/**
	 * Click Event handler for any widgets hosted by this fragment's layout
	 * 
	 * @param sender View that generated the onClick event.
	 */
	@Override
	public void onClick(final View sender) {
		if(sender.equals(addPayee)) {
			BankConductor.navigateToAddPayee(BankEnterPayeeFragment.class, null);
		}
	}
	

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

	/**
	 * Method used to display a temporary status message that notifies user that a payee from 
	 * the list has been deleted.
	 */
	public void showDeleteConfirmation() {
		if( statusView != null ) {
			statusView.setText(R.string.bank_payee_delete_confirm);
			statusView.showAndHide(DURATION);
		}
	}

	/**
	 * Method used to refresh the screen with new date downloaded.
	 * 
	 * @param extras Reference to bundle with payee list data.
	 */
	@Override
	public void refreshScreen(final Bundle extras) {
		super.refreshScreen(extras);
		
		if( extras.getBoolean(BankExtraKeys.CONFIRM_DELETE)  ) {
			this.showDeleteConfirmation();			
		}
	}		
}
