package com.discover.mobile.section.account.recent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.discover.mobile.section.account.AccountSearchTransactionFragment;

/**
 * Recent account activity fragment.  Allows the user to see details related to their transactions based
 * on a certain date range.
 * 
 * @author jthornton
 *
 */
public class AccountRecentActivityFragment extends BaseFragment {
	
	/**Text View holding the date range*/
	private TextView dateRange;
	
	/**TExt view holding the button to change to the search page*/
	private TextView searchTrans;
	
	/**Current range showing transactions*/
	private RecentActivityPeriodDetail currentRange;
	
	/**All the ranges available to be displayed*/
	private RecentActivityPeriodsDetail periods;
	
	/**Table holding the pending transactions*/
	private TransactionTable pending;
	
	/**Table holding the posted transactions*/
	private TransactionTable posted;
	
	/**Text view holding the feedback*/
	private TextView feedback;
	
	/**Load more button*/
	private Button load;
	
	/**Activity details from the server*/
	private GetTransactionDetails transactions;
	
	/**Resources*/
	private Resources res;
	
	/**Boolean letting the application know it is loading more*/
	private boolean isLoadingMore = false;
	
	/**Dialog diaplyed when server calls are made*/
	private AlertDialog dialog;
	
	/**
	 * TODO: Handle rotation
	 */
	
	/**
	 * Create the view
	 * @param inflater - used to inflate the layout
	 * @param container - container holding the view
	 * @param savedInstanceState - state of the fragment
	 * @return the view
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.account_recent_activity, null);
		res = this.getActivity().getResources();
		pending = (TransactionTable) view.findViewById(R.id.pending_transactions);
		posted = (TransactionTable) view.findViewById(R.id.posted_transactions);
		
		dateRange = (TextView) view.findViewById(R.id.view_transactions);
		dateRange.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				getNewDateRange();
			}
			
		});
		
		load = (Button) view.findViewById(R.id.load_more);
		load.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
			if(null != transactions && null != transactions.loadMoreLink)
				loadMoreTransactions(transactions.loadMoreLink);			
			}		
		});
		
		feedback = (TextView) view.findViewById(R.id.provide_feedback_button);
		feedback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showProvideFeedback();			
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
	
	/**
	 * Resume the fragment
	 */
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
	
	/**
	 * Get the date ranges to be displayed in the fragment
	 */
	private void getDateRanges(){
		showDialog();
		final  AsyncCallback<RecentActivityPeriodsDetail> callback = 
				GenericAsyncCallback.<RecentActivityPeriodsDetail>builder(this.getActivity())
				.withSuccessListener(new GetActivityPeriodsSuccessListener(this))
				.withErrorResponseHandler(new RecentActivityErrorHandler(this))
				.build();
		
		new GetActivityPeriods(getActivity(), callback).submit();
		
	}
	
	/**
	 * Get the callback for getting the transactions
	 * @return the callback for getting the transactions
	 */
	private AsyncCallback<GetTransactionDetails> getTransactionCallback(){
		return	GenericAsyncCallback.<GetTransactionDetails>builder(this.getActivity())
				.withSuccessListener(new GetTransactionsSuccessListener(this))
				.withErrorResponseHandler(new RecentActivityErrorHandler(this))
				.build();
	}
	
	/**
	 * Get the transactions for the current time period
	 */
	public void getTransactions(){
		if(null == dialog || !dialog.isShowing()){
			dialog.show();
		}
		new GetTransactions(getActivity(), getTransactionCallback(), currentRange).submit();
	}
	
	/**
	 * Get more transactions from the link
	 * @param link link to get more transactions from
	 */
	private void loadMoreTransactions(final String link){
		isLoadingMore = true;
		showDialog();
		new GetTransactions(getActivity(), getTransactionCallback(), transactions.loadMoreLink).submit();
	}
	
	/**
	 * Get a new date range for transactions
	 */
	private void getNewDateRange(){
		if(null == periods){return;}
		final ChooseDateRangeFragment fragment = new ChooseDateRangeFragment();
		fragment.setReturnFragment(this);
		fragment.setPeriods(periods);
		super.makeFragmentVisible(fragment);
	}
	
	/**
	 * Show the search screen
	 */
	protected void showSearchScreen(){
		super.makeFragmentVisible(new AccountSearchTransactionFragment());
	}
	
	/**
	 * Show the transactions retrieved from the server
	 */
	public void showTransactions(){
		hideDialog();
		if(null != transactions.loadMoreLink){
			load.setVisibility(View.VISIBLE);
		} else{
			load.setVisibility(View.GONE);
		}
		
		if(!isLoadingMore){
			clearBothTables();
		}
		
		if(transactions.showPending){
			showBothTables();
			pending.setTransactions(transactions.pending);
			pending.showTransactions(transactions.pending); 
		} else{
			showOnlyOneTable();
		}
		posted.setTransactions(transactions.posted);
		posted.showTransactions(transactions.posted);
		isLoadingMore = false;
	}
	
	/**
	 * Setup the view to only show one table
	 */
	private void showOnlyOneTable(){
		pending.setVisibility(View.GONE);
		posted.setNoTransactionsMessage(res.getString(R.string.recent_activity_no_activity));
		posted.setTitle(res.getString(R.string.recent_activity_transactions));
		pending.setTransactions(transactions.pending);
		pending.showTransactions(transactions.pending);
	}
	
	private void showBothTables(){
		pending.setVisibility(View.VISIBLE);
		posted.setTitle(res.getString(R.string.recent_activity_posted_transactions));
		pending.setTitle(res.getString(R.string.recent_activity_pending_transactions));
		pending.setNoTransactionsMessage(res.getString(R.string.recent_activity_no_new_pending));
		posted.setNoTransactionsMessage(res.getString(R.string.recent_activity_no_new_posted));
	}
	
	private void clearBothTables(){
		pending.clearList();
		posted.clearList();
	}
	
	private void showDialog(){
		if(null == dialog){
			dialog = ProgressDialog.show(this.getActivity(),
					getResources().getString(R.string.push_progress_get_title), 
					getResources().getString(R.string.push_progress_registration_loading), 
					true);
		}else{
			dialog.show();
		}
	}
	
	private void hideDialog(){
		if(null != dialog){
			dialog.dismiss();
		}
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
