package com.discover.mobile.bank.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.utils.CommonUtils;

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
	/**URL for providing feedback for card when the user is not logged in*/
	static final String CARD_PROVIDE_FEEDBACK = "https://secure.opinionlab.com/ccc01/o.asp?id=OWPeJUwo";
	
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
		
		/**Hide footer with accept button & hide header*/
		showFooter(false);
		view.findViewById(R.id.header).setVisibility(View.GONE);
		
		CommonUtils.fixBackgroundRepeat(view);
		return view;
	}
	
	@Override
	public String getTermsUrl() {
		/**Determine whether to use feedback link for Card or Bank*/
		if( AccountType.CARD_ACCOUNT == Globals.getCurrentAccount() ) {
			return CARD_PROVIDE_FEEDBACK;
		} else {
			return BankUrlManager.getProvideFeedbackUrl();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		/**This is required such that the keyboard does not overlap the provided feedback input field*/
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		/**Return to the state required by the navigation activity*/
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
		return BankMenuItemLocationIndex.PROVIDE_FEEDBACK_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PROVIDE_FEEDBACK;
	}
	@Override
	public void onAcceptClicked() {
		//this is not used in this class
	}

}
