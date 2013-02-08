package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.discover.mobile.R;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.common.bank.payee.PayeeDetail;

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
public class BankSelectPayee extends ScrollView{

	/**Activity context*/
	private final Context context;

	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to set to the view
	 */
	public BankSelectPayee(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		/**
		 * This will will have data passed to it from the bundle.
		 * This way no service call will be owned by this class.
		 */


		addView(LayoutInflater.from(context).inflate(R.layout.select_payee, null));
	}

	/**
	 * Starts the service call to get all the payees for the customer
	 */
	public void getPayees(){
		BankServiceCallFactory.createGetPayeeServiceRequest().submit();
	}

	/**
	 * Create a single choose list item
	 * @param detail - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createListItem(final PayeeDetail detail){
		final SimpleChooseListItem item =  new SimpleChooseListItem(this.context, null, detail, detail.name);
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Get the click listener for when a list item it clicked
	 * @param detail - detail that needs to be passed
	 * @return the click listener for when a list item it clicked
	 */
	private OnClickListener getOnClickListener(final PayeeDetail detail) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final Bundle bundle = new Bundle();
				bundle.putSerializable("Hey", detail);
				BankNavigator.navigateToPayBillStepTwo(bundle);
			}
		};
	}

	//	@Override
	//	public int getActionBarTitle() {
	//		return R.string.pay_a_bill_title;
	//	}
}