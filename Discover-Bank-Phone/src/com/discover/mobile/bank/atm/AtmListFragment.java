/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.bank.ui.table.TableLoadMoreFooter;

/**
 * Fragment contianing the list of details available for the ATMs
 * @author jthornton
 *
 */
public class AtmListFragment extends BaseTable{

	/**Adapter used to display data*/
	private AtmListAdapter adapter;

	/**ATMs retrieved from the server*/
	private AtmResults results;

	/**Current amount of results being shown*/
	private int index;

	/**Footer to put in the bottom of the list view*/
	private TableLoadMoreFooter footer;

	/**Fragment holding this fragment*/
	private AtmMapFragment observer;

	/**
	 * @param observer the observer to set
	 */
	public void setObserver(final AtmMapFragment observer) {
		this.observer = observer;
	}

	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		super.refreshListener();
		footer.showDone();
		results = (AtmResults)bundle.get(BankExtraKeys.DATA_LIST_ITEM);
		index = (bundle.getInt(BankExtraKeys.DATA_SELECTED_INDEX, 0));

		//If the results is empty or null
		if(null == results || null == results.results || null == results.results.atms || results.results.atms.isEmpty()){
			showFooterMessage();
			showNothingToLoad();
			return;
		}

		adapter.clear();
		adapter.setData(results.results.atms.subList(0, index));
		adapter.notifyDataSetChanged();

		if(!observer.canLoadMore()){
			showNothingToLoad();
		}
	}

	/**
	 * Report an issue with an atm
	 */
	public void reportAtm(final String id){
		observer.reportAtm(id);
	}

	/**
	 * Get the current address of the users or the search
	 * @return the current address of the users or the search
	 */
	public String getCurrentAddress(){
		return observer.getCurrentLocationAddress();
	}

	@Override
	public void setupAdapter() {
		adapter = new AtmListAdapter(this.getActivity(), R.layout.bank_atm_detail_item, this);
	}

	@Override
	public void createDefaultLists() {
		//Do no need to this
	}

	@Override
	public ArrayAdapter<?> getAdapter() {
		return adapter;
	}

	@Override
	public void maybeLoadMore() {
		if(observer.canLoadMore()){
			footer.showLoading();
			observer.loadMoreData(); 
		}
	}

	@Override
	public void setupHeader() {
		// No header for this layout
	}

	@Override
	public void setupFooter() {
		footer = new TableLoadMoreFooter(this.getActivity(), null);
		footer.getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				scrollToTop();
			}
		});
		footer.showDone();
	}

	@Override
	public View getHeader() {
		return null;
	}

	@Override
	public View getFooter() {
		return footer;
	}

	@Override
	public void goToDetailsScreen(final int index) {
		// Do nothing here, there is no detail screen
	}

	@Override
	public Bundle saveDataInBundle() {
		return null;
	}

	@Override
	public void loadDataFromBundle(final Bundle bundle) {
		// No need to do this here the observer handles this
	}

	@Override
	public void showFooterMessage() {
		footer.showEmpty(this.getString(R.string.atm_location_no_results));
	}

	@Override
	public int getActionBarTitle() {
		return R.string.atm_locator_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.REVIEW_PAYEMENTS_SECTION;
	}
}