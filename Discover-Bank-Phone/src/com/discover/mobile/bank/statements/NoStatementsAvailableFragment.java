package com.discover.mobile.bank.statements;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.fragments.BankMessageFragment;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;
/*
 * This class is used to notify the user that there are no
 * statements associated with an account for download.
 */
public class NoStatementsAvailableFragment extends  BankMessageFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	/*
	 * Methods inherited from BankMessageFragment
	 */
	@Override
	public void onClick(View v) {}

	@Override
	public String getActionButtonText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageTitle() {
		return DiscoverActivityManager.getString(R.string.no_statements_title);
	}

	@Override
	public String getBodyText() {
		return DiscoverActivityManager.getString(R.string.no_statements_body);
	}

	@Override
	public int getActionBarTitle() {
		return R.string.statements_action_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return 0;
	}

	@Override
	public int getSectionMenuLocation() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
}
