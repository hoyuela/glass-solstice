package com.discover.mobile.bank.transfer;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.help.HelpWidget;

/**
 * This Fragment shows the user information if they are not eligible for transfer money.
 * It allows them to navigate to their browser and open a new account if they wish.
 *
 * @author scottseward
 *
 */
public class BankTransferNotEligibleFragment extends BankOneButtonFragment{

	/**
	 * Setup the fragment to display the proper information.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {	
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		noteTextMsg.setText(R.string.transfer_money_not_eligible_body);
		noteTextMsg.setVisibility(View.VISIBLE);
		
		helpFooter.show(true);
		feedbackLink.setVisibility(View.GONE);
		
		actionButton.setText(R.string.open_an_account);
		actionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.external_link_icon, 0);

		actionLink.setVisibility(View.GONE);
		
		return view;
	}
	
	@Override
	protected String getPageTitle() {
		return getResources().getString(R.string.transfer_money_not_eligible_title);
	}
	
	@Override
	public void onBackPressed() {
		
	}

	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return null;
	}

	@Override
	protected void onActionButtonClick() {
		BankConductor.navigateToBrowser(BankUrlManager.getOpenAccountUrl());
	}

	@Override
	protected void onActionLinkClick() {
		
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getBankTransferHelpItems());
	}

	@Override
	public int getActionBarTitle() {
		return R.string.transfer_money;
	}

	@Override
	public int getGroupMenuLocation() {
		return 0;
	}

	@Override
	public int getSectionMenuLocation() {
		return 0;
	}

}
