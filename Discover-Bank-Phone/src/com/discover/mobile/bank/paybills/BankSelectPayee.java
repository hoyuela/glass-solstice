package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.common.BaseFragment;

/**
 * Fragment that will be used in the first step of the pay bill process.
 * This will only be shown if the user is enrolled and eligible for payments.
 * It will display the name of the payee and a button to add a new payee.
 * When features are clicked:
 * 
 * Add Payee - start add payee work flow
 * Provide feedback - start the provide feedback flow
 * Select Payee - go to the next step in the pay bills process
 * 
 * @author jthornton
 *
 */
public class BankSelectPayee extends BaseFragment{

	/**List of payees*/
	private ListPayeeDetail payees;

	/**Layout holding the payees*/
	private LinearLayout payeesList;

	/**Text view holding the empty list message*/
	private TextView empty;

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.select_payee, null);

		payeesList = (LinearLayout)view.findViewById(R.id.payee_list);
		empty = (TextView)view.findViewById(R.id.no_payees);

		if(null == savedInstanceState){
			loadListFromBundle(this.getArguments());
		} else{
			loadListFromBundle(savedInstanceState);
		}

		return view;
	}

	/**
	 * Save the state of the current fragment
	 * @param outState - bundle to save the state in.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		outState.putSerializable(BankExtraKeys.PAYEES_LIST, payees);
	}

	/**
	 * Extract the data from the bundles and then display it.
	 * @param bundle - bundle containing the data to be displayed.
	 */
	public void loadListFromBundle(final Bundle bundle){
		payees = (ListPayeeDetail)bundle.getSerializable(BankExtraKeys.PAYEES_LIST);
		if(null == payees || payees.payees.isEmpty()){
			empty.setVisibility(View.VISIBLE);
		}else{
			payeesList.removeAllViews();
			for(final PayeeDetail payee : payees.payees){
				payeesList.addView(createListItem(payee));
			}
		}
	}

	/**
	 * Create a single choose list item
	 * @param detail - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createListItem(final PayeeDetail detail){
		final SimpleChooseListItem item =  new SimpleChooseListItem(this.getActivity(), null, detail, detail.name);
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Get the click listener for when a list item it clicked
	 * @param detail - detail that needs to be passed
	 * @return the click listener for when a list item it clicked
	 */
	private OnClickListener getOnClickListener(final PayeeDetail details) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final Bundle bundle = new Bundle();
				bundle.putSerializable(BankExtraKeys.SELECTED_PAYEE, details);
				BankNavigator.navigateToPayBillStepTwo(bundle);
			}
		};
	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}
}
