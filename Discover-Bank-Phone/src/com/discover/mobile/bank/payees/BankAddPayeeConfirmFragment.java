package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;
import com.discover.mobile.bank.ui.widgets.FooterType;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment class displayed to the user after adding a Payee successfully. User will have the option to Schedule a Payment or
 * Add another Payee.
 * 
 * @author henryoyuela
 *
 */
public class BankAddPayeeConfirmFragment extends BankOneButtonFragment {
	/** 
	 * Reference to a AddPayeeDetail object used to hold the information of the Payee that will be added.
	 */
	private PayeeDetail detail = new PayeeDetail();
	/**
	 * Key used to read flag from bundle, obtained via getArguments(), to determine if confirmation page is for a update or addition.
	 */
	public static final String KEY_PAYEE_UPDATE = "update";
	/**
	 * Flag used to determine whether payee was updated or added.
	 */
	private boolean isUpdate;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		/**Fetch payee detail passed in from Step 4 in Add Payee Fragment*/
		final Bundle bundle = this.getArguments();
		if( null != bundle ) {
			if( null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
				detail = (PayeeDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);	
			}

			/**read whether this confirmation is shown for an payee update or addition*/
			isUpdate = bundle.getBoolean(KEY_PAYEE_UPDATE);
		} 

		final View view = super.onCreateView(inflater, container, savedInstanceState);

		/**initialize content to be displayed on fragment*/
		initialize(view);

		return view;

	}

	/**
	 * Method used to initialize the parent view with the content that will be displayed for this fragment.
	 * 
	 * @param mainView Reference to view created in onCreateView()
	 */
	protected void initialize(final View mainView) {
		/**Show top note to show confirmation message to user**/
		final TextView topNote = (TextView)mainView.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.VISIBLE);

		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		BankHeaderProgressIndicator progressIndicator = getProgressIndicator();
		progressIndicator.initialize(2);
		progressIndicator.hideStepTwo();
		progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);

		/**Hide Bottom Note*/
		hideBottomNote();

		getActionButton().setText(R.string.bank_sch_payment);

		/**Set footer to show privacy & terms | feedback*/
		getFooter().setFooterType(FooterType.PRIVACY_TERMS | FooterType.PROVIDE_FEEDBACK);

		/**Check if confirmation page is for a payee update or addition*/
		if( isUpdate ) {
			topNote.setText(R.string.bank_updated_confirm);

			getActionLink().setText(R.string.bank_manage_payees);
		} else {
			topNote.setText(R.string.bank_add_confirm);

			getActionLink().setText(R.string.bank_add_another);
		}
	}

	/**
	 * Method used to handle when the user taps on Schedule a Payment button
	 */
	@Override
	protected void onActionButtonClick()  {
		if( getActivity() instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)getActivity();

			//Pop everything out of the stack till we get to AccountSummary
			activity.popTillFragment(BankAccountSummaryFragment.class);

			//Navigate user to schedule a payment fragment
			BankServiceCallFactory.createGetPayeeServiceRequest().submit();
		}
	}

	/**
	 * Method used to handle when the user taps on Add Another Payee
	 */
	@Override
	protected void onActionLinkClick() {
		if( getActivity() instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)getActivity();
			activity.onBackPressed();
		}
	}

	/**
	 * Method used to return to Bank Enter Payee page.
	 */
	private void navigateBack() {
		if( getActivity() instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)getActivity();

			/**Check if confirmation page is for a payee update or addition*/
			if( isUpdate ) {
				/**
				 * Pop all fragments in stack till you get the user to BankManagePayee
				 */
				activity.popTillFragment(BankManagePayee.class);
			} else {
				/**
				 * Pop all fragments in stack till you get the user to BankEnterPayeeFragment 
				 * where they can run as search for a verified Payee again
				 */
				activity.popTillFragment(BankEnterPayeeFragment.class);
			}
		}
	}

	/**
	 * Returns list of elements created to be displayed in the body of the view. Called by super class.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getConfirmedPayeeDetailList(getActivity(), detail);
	}

	/**
	 * Method not used
	 */
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

	/**
	 * Returns the title to display in the status bar of the activity
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}

	/**
	 * Disable back press for this fragment
	 */
	@Override
	public void onBackPressed() {
		//Disable the back press
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {

		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());

	}
} 
