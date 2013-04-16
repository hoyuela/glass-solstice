package com.discover.mobile.bank.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.ui.fragments.BankMessageFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * This Fragment shows the user information if they are not eligible for transfer money.
 * It allows them to navigate to their browser and open a new account if they wish.
 *
 * @author scottseward
 *
 */
public class BankTransferNotEligibleFragment extends BankMessageFragment{

	/**
	 * Setup the fragment to display the proper information.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {	
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		actionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.external_link_icon, 0);
		actionButton.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.between_related_elements_padding));

		/**Help widget setup to show faq*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getBankTransferHelpItems());
		
		final ImageView image = (ImageView)view.findViewById(R.id.icon);
		image.setVisibility(View.GONE);
		
		
		return view;
	}
	
	
	@Override
	public String getPageTitle() {
		return getResources().getString(R.string.transfer_money_not_eligible_title);
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.transfer_money;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	@Override
	public void onClick(final View arg0) {
		BankConductor.navigateToBrowser(BankUrlManager.getOpenAccountUrl());	
	}

	@Override
	public String getActionButtonText() {
		return this.getResources().getString(R.string.open_an_account);
	}

	@Override
	public String getBodyText() {
		return this.getResources().getString(R.string.transfer_money_not_eligible_body);
	}


}
