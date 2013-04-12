package com.discover.mobile.bank.terms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Class used to display Privacy and Terms based on the type specified via the arguments bundle
 * passed to the fragment at instantiation. The Privacy and Terms HTML page displayed is dependant
 * on the PrivacyTermsType read from the arguments bundle using KEY_TERMS_TYPE.
 * 
 * 
 * @author henryoyuela
 *
 */
public class BankPrivacyTermsFragment extends TermsConditionsFragment {
	public static final String KEY_TERMS_TYPE = "terms-type";
	private PrivacyTermsType type;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( getArguments() != null ) {
			type = (PrivacyTermsType) getArguments().get(KEY_TERMS_TYPE);
		}
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
		
		/**Hide footer with accept button*/
		showFooter(false);
		
		return view;
	}
	
	@Override
	public String getTermsUrl() {
		String url = BankUrlManager.getBaseUrl();
		
		switch( type ) {
		case MobilePrivacyStatement:
			url = BankUrlManager.getPrivacyTermsUrl();
			break;
		case MobileTermsOfUse:
			url = BankUrlManager.getTermsOfUse();
			break;
		case BillPayTermsOfUse:
			url = BankUser.instance().getCustomerInfo().getPaymentsEligibility().getTermsUrl();
			break;
		case DepositTermsOfUse:
			url = BankUser.instance().getCustomerInfo().getDepositsEligibility().getTermsUrl();
			break;
		}
		
		return url;
	}

	@Override
	public int getPageTitle() {
		int title = R.string.bank_terms_privacy_n_terms;
		
		switch( type ) {
		case MobilePrivacyStatement:
			title = R.string.bank_terms_privacy_statement_title;
			break;
		case MobileTermsOfUse:
			title = R.string.bank_terms_of_use;
			break;
		case BillPayTermsOfUse:
		    title = R.string.bank_terms_bill_pay;
			break;
		case DepositTermsOfUse:
			title = R.string.bank_deposit_check_title;
			break;
		}
		return title;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_terms_privacy_n_terms;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return 0;
	}
	@Override
	public void onAcceptClicked() {
		//this is not used in this class
	}

}
