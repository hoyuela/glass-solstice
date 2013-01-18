package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;

public class AccountRecentActivityFragment extends BaseFragment {
	
	private TextView dateRange;
	
	private TextView searchTrans;
	
	
	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.account_recent_activity, null);
		
		dateRange = (TextView) view.findViewById(R.id.view_transactions);
		dateRange.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				chooseDateRange();
			}
			
		});
		
		searchTrans = (TextView) view.findViewById(R.id.search_transactions);
		searchTrans.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showSearchScreen();			
			}		
		});
		
		return view;
	}
	
	protected void chooseDateRange(){
		final ChooseDateRangeFragment fragment = new ChooseDateRangeFragment();
		fragment.setReturnFragment(this);
		super.makeFragmentVisible(fragment);
	}
	
	protected void showSearchScreen(){
		super.makeFragmentVisible(new AccountSearchTransactionFragment());
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.recent_activity_title;
	}

	public void setDateRange() {
		// TODO Auto-generated method stub
		
	}
	
}
