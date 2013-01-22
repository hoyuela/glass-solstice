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
import com.discover.mobile.common.account.recent.GetTransactionDetails;
import com.discover.mobile.common.account.recent.GetTransactions;
import com.discover.mobile.common.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.section.account.recent.GetTransactionsSuccessListener;

/**
 * Recent account activity fragment.  Allows the user to see details related to their transactions based
 * on a certain date range.
 * 
 * @author jthornton
 *
 */
public class AccountRecentActivityFragment extends BaseFragment {
	
	private TextView dateRange;
	
	private TextView searchTrans;
	
	private RecentActivityPeriodDetail currentRange;
	
	private RecentActivityPeriodsDetail periods;
	
	private GetTransactionDetails transactions;
	
	/**
	 * TODO: Handle rotation
	 */
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.account_recent_activity, null);
		
		dateRange = (TextView) view.findViewById(R.id.view_transactions);
		dateRange.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				getNewDateRange();
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
	
	@Override
	public void onResume(){
		super.onResume();
		if(null != currentRange){
			dateRange.setText(currentRange.displayDate);
			getTransactions();
		} else{
			getDateRanges();
		}	
	}
	
	private void getDateRanges(){
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
	
	public void getTransactions(){
		final AsyncCallback<GetTransactionDetails> callback = 
				GenericAsyncCallback.<GetTransactionDetails>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new GetTransactionsSuccessListener(this))
				//This will be done in US5241.  (Just here to get dates in the view).
				.withErrorResponseHandler(null)
				.build();
		
		new GetTransactions(getActivity(), callback, currentRange).submit();
	}
	
	private void loadMoreTransactions(){
		final AsyncCallback<GetTransactionDetails> callback = 
				GenericAsyncCallback.<GetTransactionDetails>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new GetTransactionsSuccessListener(this))
				//This will be done in US5241.  (Just here to get dates in the view).
				.withErrorResponseHandler(null)
				.build();
		
		new GetTransactions(getActivity(), callback, transactions.loadMoreLink).submit();
	}
	
	public void getNewDateRange(){
		if(null == periods){return;}
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
		dateRange.setText(currentRange.displayDate);
	}
	
	public void setPeriods(final RecentActivityPeriodsDetail periods) {
		this.periods = periods;	
	}

	public void setTransactions(GetTransactionDetails transactions) {
		this.transactions = transactions;
	}
}
