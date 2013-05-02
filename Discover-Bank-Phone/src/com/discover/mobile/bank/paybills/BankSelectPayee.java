package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.payees.BankEnterPayeeFragment;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

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
	final static String TAG = BankSelectPayee.class.getSimpleName();

	/**List of payees*/
	private ListPayeeDetail payees;

	/**Layout holding the payees*/
	private LinearLayout payeesList;

	/**Text view holding the empty list message*/
	private TextView empty;

	private View view;

	/**Text view holding the error message*/
	private TextView error;

	public boolean errorExists;

	public BankSelectPayee(){
		// Default constructor
	}

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.select_payee, null);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());

		payeesList = (LinearLayout)view.findViewById(R.id.payee_list);
		empty = (TextView)view.findViewById(R.id.no_payees);
		error = (TextView)view.findViewById(R.id.payee_error_text);

		setupAddNewPayeeButton(view);
		initViewWithBundleData();
		customSetup();

		return view;
	}

	/**
	 * Optional Override in subclass
	 */
	protected void customSetup(){
		// Called in onCreateView. Meant to be overridden if needed.
	}

	@Override
	public void onResume() {
		super.onResume();

		if(((BankNavigationRootActivity)getActivity()).consumeFragmentError()) {
			error.setVisibility(View.VISIBLE);
		}
	}


	/**
	 * This method will take the loaded list of payees and fill 
	 * the list on the screen with the data.
	 */
	private void initViewWithBundleData() {
		final Button addPayee = (Button) view.findViewById(R.id.add_payee);

		/** Used Cache list of Payees */
		payees = BankUser.instance().getPayees();

		/** Set Button Text to specific text when there are no payees in list */
		if (null == payees || payees.payees.isEmpty()) {
			empty.setVisibility(View.VISIBLE);
			addPayee.setText(getResources().getString(R.string.select_payee_no_payees));
		}
		/**
		 * Set Button Text to specific text when there are payees in the list,
		 * in addition populate list of payees
		 */
		else {
			addPayee.setText(getResources().getString(R.string.select_payee_another_payee));
			payeesList.removeAllViews();
			int i = 0;
			final int size = payees.payees.size();
			for (final PayeeDetail payee : payees.payees) {
				SimpleChooseListItem item = null;

				if (size == 1) {
					item = createSingleListItem(payee);
					payeesList.addView(item);
				} else if (i == 0) {
					item = createFirstListItem(payee);
					payeesList.addView(item);
				} else if (i == size - 1) {
					item = createLastListItem(payee);
					payeesList.addView(item);
				} else {
					item = createListItem(payee);
					payeesList.addView(item);
				}
				i++;
			}
		}
	}

	/**
	 * Create a single choose list item for a middle entry - dashed on top, no bottom stroke
	 * 
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createListItem(final PayeeDetail detail) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, detail, detail.nickName,
				R.layout.simple_choose_item_dash);
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Create a single choose list item for the bottom entry - solid stroke on bottom
	 * 
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createLastListItem(final PayeeDetail detail) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, detail, detail.nickName);
		item.setBackgroundAsBottomItem(getActivity());
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Create a single choose list item for a top entry - solid stroke on top
	 * 
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createFirstListItem(final PayeeDetail detail) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, detail, detail.nickName);
		item.setBackgroundAsTopItem(getActivity());
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Create a single choose list item for a single list entry - solid stroke on all sides.
	 * 
	 * @param detail
	 *            - detail used to show the text and associate to the item
	 * @return the single choose list item
	 */
	private SimpleChooseListItem createSingleListItem(final PayeeDetail detail) {
		final SimpleChooseListItem item = new SimpleChooseListItem(
				this.getActivity(), null, detail, detail.nickName);
		item.setOnClickListener(getOnClickListener(detail));
		return item;
	}

	/**
	 * Get the click listener for when a list item it clicked
	 * @param detail - detail that needs to be passed
	 * @return the click listener for when a list item it clicked
	 */
	protected OnClickListener getOnClickListener(final PayeeDetail details) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final Bundle bundle = new Bundle();
				bundle.putSerializable(BankExtraKeys.SELECTED_PAYEE, details);
				BankConductor.navigateToPayBillStepTwo(bundle);
			}
		};
	}

	/**
	 * Setup the add new payee button to navigate to the add new payee flow when clicked
	 */
	private void setupAddNewPayeeButton(final View view) {
		final Button addPayee = (Button)view.findViewById(R.id.add_payee);
		addPayee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToAddPayee(BankEnterPayeeFragment.class, null);
			}
		});
	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

	/**
	 * @return the payees
	 */
	public ListPayeeDetail getPayees() {
		return payees;
	}

	/**
	 * @return the payeesList
	 */
	public LinearLayout getPayeesList() {
		return payeesList;
	}

	/**
	 * @return the empty
	 */
	public TextView getEmpty() {
		return empty;
	}

	/**
	 * @return the view
	 */
	@Override
	public View getView() {
		return view;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_SECTION;
	}

	/**
	 * Method used to refresh the screen with new date downloaded.
	 * 
	 * @param extras Reference to bundle with payee list data.
	 */
	public void refreshScreen(final Bundle extras) {
		initViewWithBundleData();
		customSetup();
	}

}
