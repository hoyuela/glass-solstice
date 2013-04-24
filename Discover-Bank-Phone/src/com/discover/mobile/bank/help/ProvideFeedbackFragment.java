package com.discover.mobile.bank.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;
import com.discover.mobile.common.Globals;
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
public class ProvideFeedbackFragment extends TermsConditionsFragment {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		if( !Globals.isLoggedIn() ) {
			help.showHelpItems(HelpMenuListFactory.instance().getLoggedOutHelpItems());
		} else {
			help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
		}
		
		/**Hide footer with accept button*/
		showFooter(false);
		
		return view;
	}
	
	@Override
	public String getTermsUrl() {
		return BankUrlManager.getProvideFeedbackUrl();
	}

	@Override
	public int getPageTitle() {
		return R.string.bank_provide_feedback;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_provide_feedback;
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
