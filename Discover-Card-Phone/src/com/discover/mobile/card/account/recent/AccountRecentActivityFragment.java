package com.discover.mobile.card.account.recent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.AccountSearchTransactionFragment;
import com.discover.mobile.card.account.summary.LatePaymentModalTop;
import com.discover.mobile.card.services.account.recent.GetActivityPeriods;
import com.discover.mobile.card.services.account.recent.GetTransactionDetails;
import com.discover.mobile.card.services.account.recent.GetTransactions;
import com.discover.mobile.card.services.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.card.services.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.widgets.ExtendingScrollView;

/**
 * Recent account activity fragment. Allows the user to see details related to
 * their transactions based on a certain date range.
 * 
 * @author jthornton
 * 
 */
public class AccountRecentActivityFragment extends BaseFragment {

	/** Text View holding the date range */
	private TextView dateRange;

	/** Current range showing transactions */
	private RecentActivityPeriodDetail currentRange;

	/** All the ranges available to be displayed */
	private RecentActivityPeriodsDetail periods;

	/** Table holding the pending transactions */
	private TransactionTable pending;

	/** Table holding the posted transactions */
	private TransactionTable posted;

	/** Activity details from the server */
	private GetTransactionDetails transactions;

	/** Resources */
	private Resources res;

	/** Boolean letting the application know it is loading more */
	private boolean isLoadingMore = false;

	/** Dialog diaplyed when server calls are made */
	private AlertDialog dialog;

	/**
	 * TODO: Handle rotation Need to save categories, current transactions on
	 * screen
	 */

	/**
	 * Create the view
	 * 
	 * @param inflater
	 *            - used to inflate the layout
	 * @param container
	 *            - container holding the view
	 * @param savedInstanceState
	 *            - state of the fragment
	 * @return the view
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.account_recent_activity,
				null);
		res = this.getActivity().getResources();
		pending = (TransactionTable) view
				.findViewById(R.id.pending_transactions);
		posted = (TransactionTable) view.findViewById(R.id.posted_transactions);

		dateRange = (TextView) view.findViewById(R.id.view_transactions);
		dateRange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				getNewDateRange();
			}

		});

		final TextView feedback = (TextView) view.findViewById(R.id.provide_feedback_button);
		feedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showProvideFeedback();
			}
		});

		/** TExt view holding the button to change to the search page */
		final TextView searchTrans = (TextView) view.findViewById(R.id.search_transactions);
		searchTrans.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showSearchScreen();
			}
		});

		/** ScrollView element in which the page is contained */
		final ExtendingScrollView scrollView = (ExtendingScrollView) view;
		scrollView.attachFragment(this);

		return view;
	}

	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (RecentActivityRotationHelper.getHelper().isHasData()) {
			resumeFragment();
			dateRange.setText(currentRange.displayDate);
		} else if (null != currentRange) {
			dateRange.setText(currentRange.displayDate);
			getTransactions();
		} else {
			getDateRanges();
		}
	}

	/**
	 * Resume the fragment from its previous state
	 */
	private void resumeFragment() {
		final RecentActivityRotationHelper helper = RecentActivityRotationHelper
				.getHelper();
		this.currentRange = helper.getCurrentRange();
		this.pending.showTransactions(helper.getPending());
		this.posted.showTransactions(helper.getPosted());
		this.periods = helper.getPeriods();
		this.transactions = helper.getTransactions();
		helper.clearHelper();
		showTransactions();
	}

	/**
	 * Save the sate of the fragment
	 * 
	 * @param outState
	 *            - bundle to save the state in
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		final RecentActivityRotationHelper helper = RecentActivityRotationHelper
				.getHelper();
		helper.setCurrentRange(currentRange);
		helper.setPending(pending.getTransactions());
		helper.setPeriods(periods);
		helper.setPosted(posted.getTransactions());
		helper.setTransactions(transactions);
		helper.setHasData(true);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Get the date ranges to be displayed in the fragment
	 */
	private void getDateRanges() {
		showDialog();
		final AsyncCallback<RecentActivityPeriodsDetail> callback = GenericAsyncCallback
				.<RecentActivityPeriodsDetail> builder(this.getActivity())
				.withSuccessListener(
						new GetActivityPeriodsSuccessListener(this))
						.withErrorResponseHandler(new RecentActivityErrorHandler(this))
						.withExceptionFailureHandler(new BaseExceptionFailureHandler())
						.build();

		new GetActivityPeriods(getActivity(), callback).submit();

	}

	/**
	 * Get the callback for getting the transactions
	 * 
	 * @return the callback for getting the transactions
	 */
	private AsyncCallback<GetTransactionDetails> getTransactionCallback() {
		return GenericAsyncCallback
				.<GetTransactionDetails> builder(this.getActivity())
				.withSuccessListener(new GetTransactionsSuccessListener(this))
				.withErrorResponseHandler(new RecentActivityErrorHandler(this))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.build();
	}

	/**
	 * Get the transactions for the current time period
	 */
	public void getTransactions() {
		if (null == dialog || !dialog.isShowing()) {
			showDialog();
		}
		new GetTransactions(getActivity(), getTransactionCallback(),
				currentRange).submit();
	}

	/**
	 * Get more transactions from the link
	 * 
	 * @param link
	 *            link to get more transactions from
	 */
	private void loadMoreTransactions(final String link) {
		isLoadingMore = true;
		showDialog();
		new GetTransactions(getActivity(), getTransactionCallback(),
				transactions.loadMoreLink).submit();
	}

	/**
	 * Get a new date range for transactions
	 */
	private void getNewDateRange() {
		if (null == periods) {
			return;
		}
		final ChooseDateRangeFragment fragment = new ChooseDateRangeFragment();
		fragment.setReturnFragment(this);
		fragment.setPeriods(periods);
		super.makeFragmentVisible(fragment);
	}

	/**
	 * Show the error modal, used when the server call fails
	 */
	public void showErrorModal() {
		hideDialog();
		final Context context = this.getActivity();
		final LatePaymentModalTop top = new LatePaymentModalTop(context, null);
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(
				context, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(
				context, top, bottom);
		top.setErrorState();
		bottom.setButtonText(R.string.account_summary_modal_button);
		bottom.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modal.dismiss();
				popStack();
			}
		});
		super.showCustomAlertDialog(modal);
	}

	/**
	 * Pop the stack so that the user does not see a data less page
	 */
	protected void popStack() {
		this.getFragmentManager().popBackStack();
	}

	/**
	 * Show the search screen
	 */
	protected void showSearchScreen() {
		super.makeFragmentVisible(new AccountSearchTransactionFragment());
	}

	/**
	 * Show the transactions retrieved from the server
	 */
	public void showTransactions() {

		if (!isLoadingMore) {
			clearBothTables();
		}

		if (transactions.showPending) {
			showBothTables();
			pending.setTransactions(transactions.pending);
			pending.showTransactions(transactions.pending);
		} else {
			showOnlyOneTable();
		}
		posted.setTransactions(transactions.posted);
		posted.showTransactions(transactions.posted);
		isLoadingMore = false;
		hideDialog();
	}

	/**
	 * Setup the view to only show one table
	 */
	private void showOnlyOneTable() {
		pending.setVisibility(View.GONE);
		posted.setNoTransactionsMessage(res
				.getString(R.string.recent_activity_no_activity));
		posted.setTitle(res.getString(R.string.recent_activity_transactions));
		pending.setTransactions(transactions.pending);
		pending.showTransactions(transactions.pending);
	}

	/**
	 * Setup the view to show both tables
	 */
	private void showBothTables() {
		pending.setVisibility(View.VISIBLE);
		posted.setTitle(res
				.getString(R.string.recent_activity_posted_transactions));
		pending.setTitle(res
				.getString(R.string.recent_activity_pending_transactions));
		pending.setNoTransactionsMessage(res
				.getString(R.string.recent_activity_no_new_pending));
		posted.setNoTransactionsMessage(res
				.getString(R.string.recent_activity_no_new_posted));
	}

	/**
	 * Clear all layouts in both tables
	 */
	private void clearBothTables() {
		pending.clearList();
		posted.clearList();
	}

	/**
	 * Show the progress dialog
	 */
	private void showDialog() {
		if (null == dialog) {
			dialog = ProgressDialog.show(
					this.getActivity(),
					getResources().getString(R.string.push_progress_get_title),
					getResources().getString(
							R.string.push_progress_registration_loading), true);
		} else {
			dialog.show();
		}
	}

	/**
	 * Hide the progress dialog
	 */
	private void hideDialog() {
		if (null != dialog) {
			dialog.dismiss();
		}
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the
	 * title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.recent_activity_title;
	}

	/**
	 * Set the current date range to be displayed in the fragment
	 * 
	 * @param recentActivityPeriodDetail
	 *            - the current date range to be displayed in the fragment
	 */
	public void setDateRange(
			final RecentActivityPeriodDetail recentActivityPeriodDetail) {
		currentRange = recentActivityPeriodDetail;
		dateRange.setText(currentRange.displayDate);
	}

	/**
	 * Set the periods that can be displayed in the fragment
	 * 
	 * @param periods
	 *            - Set the periods that can be displayed in the fragment
	 */
	public void setPeriods(final RecentActivityPeriodsDetail periods) {
		this.periods = periods;
	}

	/**
	 * Set the transactions that can be displayed in the fragment
	 * 
	 * @param transactions
	 *            - set the transactions that can be displayed in the fragment
	 */
	public void setTransactions(final GetTransactionDetails transactions) {
		this.transactions = transactions;
	}

	/**
	 * Loads more transactions when the user has scrolled to the bottom and
	 * subsequently tried to scroll beyond that.
	 */
	@Override
	public void scrollViewBottomReached() {
		if (null != transactions && null != transactions.loadMoreLink) {
			loadMoreTransactions(transactions.loadMoreLink);
		}
	}
}
