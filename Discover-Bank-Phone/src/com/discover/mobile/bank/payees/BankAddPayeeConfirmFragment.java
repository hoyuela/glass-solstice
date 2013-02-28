package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

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
	AddPayeeDetail detail = new AddPayeeDetail();
	 
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		/**Fetch payee detail passed in from Step 4 in Add Payee Fragment*/
		final Bundle bundle = this.getArguments();
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			detail =  (AddPayeeDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);				
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
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)mainView.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		this.progressIndicator.initChangePasswordHeader(2);
		this.progressIndicator.hideStepTwo();
		this.progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);
			
		/**Hide Bottom Note*/
		this.noteTitle.setVisibility(View.GONE);
		this.noteTextMsg.setVisibility(View.GONE);
		
		this.actionButton.setText(R.string.bank_sch_payment);
		
		this.actionLink.setText(R.string.bank_add_another);
	}
	
	/**
	 * Method used to handle when the user taps on Schedule a Payment button
	 */
	@Override
	protected void onActionButtonClick()  {
		if( getActivity() instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)getActivity();
			/**
			 * Remove this fragment from the transactions list, this seems to be required since 
			 * makeVisible(fragment, boolean) was used.
			 */
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
			
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
			/**
			 * Remove this fragment from the transactions list, this seems to be required since 
			 * makeVisible(fragment, boolean) was used.
			 */
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
			
			//Pop all fragments in stack till you get the user to BankEnterPayeeFragment where they can run as search for a verified Payee again
			activity.popTillFragment(BankEnterPayeeFragment.class);
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the title to display in the status bar of the activity
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}
} 
