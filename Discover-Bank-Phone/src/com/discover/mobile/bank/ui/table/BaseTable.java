package com.discover.mobile.bank.ui.table;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Base table item.  Forces the sub classes to implement exactly what it needs
 * to be created and displayed. 
 * 
 * @author jthornton
 *
 */
public abstract class BaseTable extends BaseFragment  implements DynamicDataFragment{

	/**List View holding the data*/
	private PullToRefreshListView table;

	/**Bundle to load data from*/
	private Bundle loadBundle;

	/**Boolean used to determine if the fragment is loading more*/
	private boolean isLoadingMore = false;

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_list, null);
		table = (PullToRefreshListView) view.findViewById(R.id.bank_table);

		createDefaultLists();
		setupHeader();
		setupFooter();
		setupAdapter();
		return view;
	}

	/**
	 * Set up the table
	 */
	private void setUpTable(){
		table.setMode(Mode.PULL_FROM_END);
		table.setShowDividers(R.drawable.table_dotted_line);
		table.getLoadingLayoutProxy().setLoadingDrawable(null);
		table.getLoadingLayoutProxy().setPullLabel("");
		table.getLoadingLayoutProxy().setRefreshingLabel("");
		table.getLoadingLayoutProxy().setReleaseLabel("");
		table.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
				table.setShowViewWhileRefreshing(false);
				maybeLoadMore();

			}
		});
	}


	/**
	 * Set up the adapter for this list
	 */
	public abstract void setupAdapter();

	/**
	 * Setup the lists of details that are not already created
	 */
	public abstract void createDefaultLists();

	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();
		setUpTable();
		final Bundle bundle = BankRotationHelper.getHelper().getBundle();
		loadBundle = (null == bundle) ? this.getArguments() : bundle;
		loadDataFromBundle(loadBundle);
		table.setAdapter(getAdapter());
	}

	/**
	 * Save all the data on the screen in a bundle
	 * @param outState -  bundle containing all the data
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		BankRotationHelper.getHelper().setBundle(saveDataInBundle());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Get the adapter that needs to be attached to the fragment.
	 * @param adatper - adapter to be attached to the list
	 */
	public abstract ArrayAdapter<?> getAdapter();

	/**
	 * Method that is called when the adapter gets to the bottom of the list.  
	 * This will show the go to top or show the loading bar for most fragments.
	 */
	public abstract void maybeLoadMore();

	/**
	 * Set up the header
	 */
	public abstract void setupHeader();

	/**
	 * Set up the footer
	 */
	public abstract void setupFooter();

	/**
	 * Get the header that should be shown at the top of the list.
	 */
	public abstract View getHeader();

	/**
	 * Get the footer that should be shown at the top of the list.
	 */
	public abstract View getFooter();

	/**
	 * Go to the details screen associated with this view
	 * @param index - index to pass to the detail screen
	 */
	public abstract void goToDetailsScreen(final int index);

	/**
	 * Save all the data on the screen in a bundle
	 * @return bundle containing all the data
	 */
	public abstract Bundle saveDataInBundle();

	/**
	 * Extract all the data from a bundle
	 * @param bundle - bundle to pull data from
	 */
	public abstract void loadDataFromBundle(final Bundle bundle);

	/**
	 * Scroll the list to the top of the layout.  
	 * This is a little hacky, there is an issue with scroll to top
	 */
	protected void scrollToTop(){
		table.setAdapter(null);
		table.setAdapter(getAdapter());
		getAdapter().notifyDataSetChanged();
	}

	/**
	 * Show the empty message in the footer
	 * @return 
	 */
	public abstract void showFooterMessage();

	/**
	 * Set if the fragment is loading more
	 * @param isLoadingMore - if the fragment is loading more
	 */
	@Override
	public void setIsLoadingMore(final boolean isLoadingMore){
		this.isLoadingMore = isLoadingMore;
	}

	/**
	 * Get if the fragment is loading more
	 * @return isLoadingMore - if the fragment is loading more
	 */
	@Override
	public boolean getIsLoadingMore(){
		return isLoadingMore;
	}

	/**
	 * Refresh the pull to reset listener so that it can load more
	 */
	public void refreshListener(){
		table.onRefreshComplete();
		table.setShowViewWhileRefreshing(true);
	}

	/**
	 * Stop the table from showing that it can be refreshed
	 */
	public void showNothingToLoad(){
		table.setMode(Mode.DISABLED);
	}

}
