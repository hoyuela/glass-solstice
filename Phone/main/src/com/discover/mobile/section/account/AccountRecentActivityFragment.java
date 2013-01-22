package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.common.account.recent.GetActivityPeriods;
import com.discover.mobile.common.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;

/**
 * Recent account activity fragment.  Allows the user to see details related to their transactions based
 * on a certain date range.
 * 
 * *********
 * Note this class is done in a different user story (US5241) - this will be commented after the user story. 
 * This had to be created to reach the choose date fragment
 * *********
 * @author jthornton
 *
 */
public class AccountRecentActivityFragment extends BaseFragment {
	
	private TextView dateRange;
	
	private TextView searchTrans;
	
	private RecentActivityPeriodDetail currentRange;
	
	
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
		final AsyncCallback<RecentActivityPeriodsDetail> callback = 
				GenericAsyncCallback.<RecentActivityPeriodsDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new GetActivityPeriodsSuccessListener(this))
				//This will be done in US5241.  (Just here to get dates in the view).
				.withErrorResponseHandler(null)
				.build();
		
		new GetActivityPeriods(getActivity(), callback).submit();
		
	}
	
	public void getNewDateRange(final RecentActivityPeriodsDetail periods){
		final ChooseDateRangeFragment fragment = new ChooseDateRangeFragment();
		fragment.setReturnFragment(this);
		fragment.setPeriods(periods);
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

	public void setDateRange(final RecentActivityPeriodDetail recentActivityPeriodDetail) {
		currentRange = recentActivityPeriodDetail;	
		//TODO: Do server call to get transactions
	}
}