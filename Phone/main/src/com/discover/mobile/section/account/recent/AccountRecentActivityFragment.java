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
	
	private TextView dateRange;
	
	private TextView searchTrans;
	
	private RecentActivityPeriodDetail currentRange;
	
	private RecentActivityPeriodsDetail periods;
	
	private TransactionTable pending;
	
	private TransactionTable posted;
	
	private TextView feedback;
	
	private Button load;
	
	private GetTransactionDetails transactions;
	
	private Resources res;
	
	private boolean isLoadingMore = false;
	
	private AlertDialog dialog;
	
	/**
	 * TODO: Handle rotation
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
		showDialog();
		//TODO: Create error handler
		final  AsyncCallback<RecentActivityPeriodsDetail> callback = 
				GenericAsyncCallback.<RecentActivityPeriodsDetail>builder(this.getActivity())
				.withSuccessListener(new GetActivityPeriodsSuccessListener(this))
				.withErrorResponseHandler(null)
				.build();
		
		new GetActivityPeriods(getActivity(), callback).submit();
		
	}
	
	private AsyncCallback<GetTransactionDetails> getTransactionCallback(){
		return	GenericAsyncCallback.<GetTransactionDetails>builder(this.getActivity())
				.withSuccessListener(new GetTransactionsSuccessListener(this))
				.withErrorResponseHandler(null)
				.build();
	}
	
	public void getTransactions(){
		if(null == dialog || !dialog.isShowing()){
			dialog.show();
		}
		new GetTransactions(getActivity(), getTransactionCallback(), currentRange).submit();
	}
	
	private void loadMoreTransactions(final String link){
		isLoadingMore = true;
		showDialog();
		new GetTransactions(getActivity(), getTransactionCallback(), transactions.loadMoreLink).submit();
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
