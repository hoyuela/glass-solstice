package com.discover.mobile.bank.deposit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment used to display the Check Deposit - Not Eligible User Page. It uses the layout defined
 * in res/layout/bank_deposit_not_eligible.xml.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositNotEligibleFragment extends BaseFragment implements OnClickListener  {
	/**
	 * Reference to footer in layout
	 */
	private BankLayoutFooter footer;

	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_deposit_not_eligible, null);
		
		/**Create footer that will listen when user taps on Need Help Number to dial*/
		footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.setHelpNumber(getString(com.discover.mobile.bank.R.string.bank_deposit_noteligible_number));
		
		/**Set the fragment as the handler for the button click event*/
		final Button openAccountBtn = (Button)view.findViewById(R.id.openAccount);		
		openAccountBtn.setOnClickListener(this);
		
		/**Help widget setup to show faq*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
		
		return view;
	}
	
	/**
	 * Method called by base class in onCreateView to determine what string to display in the action bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_deposit_title;
	}


	/**
	 * Method used to retrieve menu group this fragment class is associated with.
	 */
	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP;
	}

	/**
	 * Method used to retreive the menu section this fragment class is associated with.
	 */
	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_NOW_SECTION;
	}	
	
	@Override
	public void onClick(final View arg0) {
		BankConductor.navigateToBrowser(BankUrlManager.getOpenAccountUrl());	
	}
}
