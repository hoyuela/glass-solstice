/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.atm.AtmDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * Fragment containing the list of details available for the ATMs
 * @author jthornton
 *
 */
public class AtmListFragment extends BaseTable implements FragmentOnBackPressed{

	/**Adapter used to display data*/
	private AtmListAdapter adapter;

	/**Fragment holding this fragment*/
	private AtmMapFragment observer;

	/**Table title header*/
	private TableTitles header;

	/**Delay amount for the load more thread*/
	private static final int DELAY = 200;

	/**
	 * @param observer the observer to set
	 */
	public void setObserver(final AtmMapFragment observer) {
		this.observer = observer;
	}

	@Override
	public void handleReceivedData(final Bundle bundle) {
		/**ATMs retrieved from the server*/
		AtmResults results = (AtmResults)bundle.get(BankExtraKeys.DATA_LIST_ITEM);
		/**Current amount of results being shown*/
		int index = (bundle.getInt(BankExtraKeys.DATA_SELECTED_INDEX, 0));

		//If the results is empty or null
		if(null == results || null == results.results || null == results.results.atms || results.results.atms.isEmpty()){
			showNothingToLoad();
			header.setMessage(this.getString(R.string.atm_location_no_results));
			footer.hideAll();
			return;
		}else{
			header.hideMessage();
			header.hideFilters();
			footer.showDone();
		}

		adapter.clear();
		adapter.setData(results.results.atms.subList(0, index));
		adapter.notifyDataSetChanged();

		setIsLoadingMore(false);
		footer.showDone();
		super.refreshListener();

		if(!observer.canLoadMore()){
			showNothingToLoad();
		}else{
			table.setMode(Mode.PULL_FROM_END);
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
			//Delay the loading of the load more so that the listener has time to refresh.
			final Handler handler = new Handler(); 
			handler.postDelayed(new Runnable() { 
				@Override
				public void run() { 
					observer.loadMoreData(); 
				} 
			}, DELAY); 
		}
	}

	@Override
	public void setupHeader() {
		header = new TableTitles(this.getActivity(), null);
		header.setMessage(this.getString(R.string.atm_location_no_results));
		header.hideFilters();
		showNothingToLoad();
	}

	@Override
	public void setupFooter() {
		footer.getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				scrollToTop();
			}
		});
		footer.hideAll();
	}

	/**
	 * Show the street view
	 */
	public void showStreetView(final AtmDetail atm){
		try {
			final String addressString =  atm.address1 + " "  + atm.city +" " + atm.state;
			final Geocoder coder = new Geocoder(this.getActivity());
			final List<Address> addresses = coder.getFromLocationName(addressString, 1);
			final Bundle bundle = new Bundle();
			if(null == addresses || addresses.isEmpty()){
				bundle.putDouble(BankExtraKeys.STREET_LAT, addresses.get(0).getLatitude());
				bundle.putDouble(BankExtraKeys.STREET_LON, addresses.get(0).getLongitude());						
			}else{
				bundle.putDouble(BankExtraKeys.STREET_LAT, atm.getLatitude());
				bundle.putDouble(BankExtraKeys.STREET_LON, atm.getLongitude());	
			}
			bundle.putInt(BankExtraKeys.ATM_ID, atm.id);
			observer.showStreetView(bundle);
		} catch (final IOException e) {
			if(Log.isLoggable(AtmMarkerBalloonManager.class.getSimpleName(), Log.ERROR)){
				Log.e(AtmMarkerBalloonManager.class.getSimpleName(), "Error Getting Street View:" + e);
			}
		}
	}

	@Override
	public View getHeader() {
		return header;
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
		// 
	}

	@Override
	public int getActionBarTitle() {
		return R.string.atm_locator_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ATM_LOCATOR_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return (observer instanceof SearchNearbyFragment) 
				? BankMenuItemLocationIndex.FIND_NEARBY_SECTION: BankMenuItemLocationIndex.SEARCH_BY_LOCATION;
	}

	@Override
	public void onBackPressed() {
		observer.onBackPressed();		
	}

	@Override
	public boolean isBackPressDisabled() {
		return observer.isBackPressDisabled();
	}
}