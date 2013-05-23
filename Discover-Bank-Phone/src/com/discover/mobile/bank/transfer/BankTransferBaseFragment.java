package com.discover.mobile.bank.transfer;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;

/**
 * This is the base fragment for all Transfer Money Fragments that need a progress indicator.
 * @author scottseward
 *
 */
public abstract class BankTransferBaseFragment extends BankOneButtonFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		final BankHeaderProgressIndicator progressIndicator = getProgressIndicator();
		progressIndicator.initialize(getProgressIndicatorStep());
		progressIndicator.setTitle(R.string.transfer_money, R.string.empty, R.string.confirm);

        progressIndicator.hideStepTwo();
        
		return view;
	}
	
	protected abstract int getProgressIndicatorStep();
	
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

	}

	@Override
	protected void onActionLinkClick() {

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

}
