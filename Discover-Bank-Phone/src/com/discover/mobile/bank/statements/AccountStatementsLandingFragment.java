package com.discover.mobile.bank.statements;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
/*
 * This class shows the statements landing page for a particular 
 * account.  At current moment, this class is only used to test navigation it
 * does not actually display any statements.
 */
public class AccountStatementsLandingFragment extends BankOneButtonFragment{

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//Used s
		getPageTitleView().setText("account landing page");
		getPageTitleView().setVisibility(View.VISIBLE);
		return view;
	}
	
	@Override
	public void onBackPressed() {}

	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onActionButtonClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onActionLinkClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getActionBarTitle() {
		return R.string.statements_action_title;
	}

	@Override
	public int getGroupMenuLocation() {
		//View statements is the first group
		return 0;
	}

	@Override
	public int getSectionMenuLocation() {
		//second in the group list
		return 2;
	}

}
