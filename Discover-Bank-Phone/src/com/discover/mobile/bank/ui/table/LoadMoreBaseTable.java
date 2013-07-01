package com.discover.mobile.bank.ui.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.transfer.GetTransfersServiceCall;
import com.discover.mobile.bank.services.transfer.ListTransferDetail;
import com.discover.mobile.bank.transfer.ViewHolder;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.utils.StringUtility;
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
public abstract class LoadMoreBaseTable extends BaseFragment  implements DynamicDataFragment, LoadMoreList, Serializable {

	private static final long serialVersionUID = -1476233454449735956L;

	private final int availableCores = Runtime.getRuntime().availableProcessors();
	private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(availableCores, 
																		 availableCores, 
																		 1, 
																		 TimeUnit.MINUTES,
																		 new LinkedBlockingQueue<Runnable>());
	
	private static final String SELECTED_BUTTON_KEY = LoadMoreBaseTable.class.getSimpleName() + "index";
	private static final String SCROLL_POSITION = "sy";
	private static final String CACHED_MAP = "chm";
	private static final String LAST_VISIBLE_LIST_ITEM = "lvli";
	
	private int savedIndex = 0;
	private int savedScroll = 0;
	private int savedTopElement = 0;
	
	private Enum<?> currentListKey = null;

	private final HashMap<Enum<?>, LoadMoreList> tableListsCache = new HashMap<Enum<?>, LoadMoreList>();
	
	private LoadMoreTableAdapter tableAdapter = null;

	/**List View holding the data*/
	private PullToRefreshListView table;

	/**Footer of the table*/
	private TableLoadMoreFooter footer;

	/**Boolean used to determine if the fragment is loading more*/
	private boolean isLoadingMore = false;
	
	/**The table headaer*/
	private LoadMoreTableHeader header = null;
	
	private Handler uiHandler = null;

	//-------------------------------------------------- Abstract Methods --------------------------------------------------
	public abstract String getEmptyListMessageForEnum(final Enum<?> listType);
	protected abstract void addButtonsToHeader(final LoadMoreTableHeader header);
	protected abstract void setTableTitles(final TableTitles titleRow);
	/**
	 * Go to the details screen associated with this view
	 * @param index - index to pass to the detail screen
	 */
	public abstract void goToDetailsScreen(final int index);

	/**
	 * Show the empty message in the footer
	 * @return 
	 */
	public abstract void showFooterMessage();
	
	public abstract Enum<?> getDefaultList();
	public abstract void setDefaultList(final Enum<?> defaultList);
	
	//-------------------------------------------------- Public Methods --------------------------------------------------
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
		footer = (TableLoadMoreFooter) view.findViewById(R.id.footer);
		uiHandler = new Handler(Looper.getMainLooper());
	
		final Bundle args = getArguments();
		if(args != null) {
			restoreArgumentsData(args);
			loadDefaultListToCache(args);
		}
		
		setupFooter();
		setUpTable();
		return view;
	}
	
	/**
	 * Save all the data on the screen in a bundle
	 * @param outState -  bundle containing all the data
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);

		final Bundle args = getArguments();
		
		//Save all of the Bundle data from the saveDataInBundle method to
		//the arguments bundle that belongs to this Fragment.
		if(args != null){
			args.putAll(outState);
			saveTableScrollPositionToBundle(args);
			
			if(header != null) {
				args.putInt(SELECTED_BUTTON_KEY, header.getSelectedButtonIndex());
			}
			
			args.putSerializable(BankExtraKeys.CACHE_KEY, currentListKey);
			args.putSerializable(CACHED_MAP, tableListsCache);
		}
	}
	
	/**
	 * If the current list, which maps to the current key in cache, has more data to load.
	 */
	@Override
	public boolean canLoadMore() {
		return getCurrentList() != null && getCurrentList().canLoadMore();
	}
	
	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		setIsLoadingMore(true);
	
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.LOAD_MORE_LIST, LoadMoreBaseTable.this);
		bundle.putSerializable(BankExtraKeys.CACHE_KEY, currentListKey);
		bundle.putBoolean(BankExtraKeys.IS_LOADING_MORE, true);
		final GetTransfersServiceCall call = BankServiceCallFactory.createBankGetTransfersCall(url);
		call.getExtras().putAll(bundle);
			
		call.submit();
	}

	/**
	 * Set up the footer
	 */
	public void setupFooter() {
		getLoadMoreFooter().getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				uiHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(table != null && table.getRefreshableView() != null) {
							table.getRefreshableView().smoothScrollToPosition(0);
						}
					}
				});
			}
		});
		getLoadMoreFooter().showDone();
	}
	
	/**
	 * Will setup the listview for a particular list that mapps to the parameter key.
	 * @param tableDataCategory an Enumerated type that maps to a list in cache.
	 */
	public void refreshTableAdapterForType(final Enum<?> tableDataCategory) {
		if(getActivity() != null && table != null) {
			currentListKey = tableDataCategory;
			final boolean isLocalCacheEmpty = tableListsCache.get(currentListKey) == null;
			LoadMoreList cachedList = null;
			
			//If local cache is empty
			if(isLocalCacheEmpty) {
				tableListsCache.put(currentListKey, BankUser.instance().getCachedActivityMap().get(currentListKey));
			}
			
			cachedList = tableListsCache.get(currentListKey);
						
			tableAdapter.setData(cachedList);
			tableAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * During a service call, this method is used to clear the list so that no data is presented behind
	 * the loading dialog during the load.
	 */
	public void showEmptyTableForServiceCall() {
		final ListTransferDetail emptyList = new ListTransferDetail();
		tableAdapter.setData(emptyList);
		tableAdapter.notifyDataSetChanged();
		hideEmptyListMessage();
	}
	
	/**
	 * This method is called once a load more operation succeeds. It will append data to the cached list
	 * that corresponds to the current cache key.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addData(final Bundle data) {
		threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				if(data != null) {
					final Enum<?> listKey = (Enum<?>)data.getSerializable(BankExtraKeys.CACHE_KEY);
					final LoadMoreList retrievedData = (LoadMoreList)data.getSerializable(LoadMoreList.APPEND_LIST_KEY);

					if(listKey != null && retrievedData != null) {
						final LoadMoreList cachedList = tableListsCache.get(listKey);
						if(cachedList != null) {
							//Get the new data that will be added to the current list.
							final List<LoadMoreDetail> appendableData = (List<LoadMoreDetail>)retrievedData.getDataList();
							//The current list that will have data added to it.
							final List<LoadMoreDetail> cachedListData = (List<LoadMoreDetail>)cachedList.getDataList();
							
							final boolean canAppend = cachedListData != null && 
													  appendableData != null && 
													  appendableData.size() > 0;

						    //Update the links object of the cached list to be that of the newly retrieved list.
							if(canAppend && retrievedData.getLinks() != null) {
								cachedList.setLinks(retrievedData.getLinks());
							}
							
							if(canAppend) {
								cachedListData.addAll(appendableData);
								uiHandler.post(new Runnable() {
									
									@Override
									public void run() {
										if(tableAdapter != null) {
											tableAdapter.notifyDataSetChanged();
										}
									}
								});
							} else {
								Log.e(LoadMoreBaseTable.class.getSimpleName(), 
										"Failed to append any data.");
							}
							
						}else {
							Log.e(LoadMoreBaseTable.class.getSimpleName(),
									"No data returned from loadMore call.");
						}
					} else {
						Log.e(LoadMoreBaseTable.class.getSimpleName(),
								"Cannot append more data without a hash key.");
					}
				}else {
					Log.e(LoadMoreBaseTable.class.getSimpleName(),
							"Attempted to addData without a Bundle of data.");
				}
				Looper.loop();
			}
		});
	}
	
	/**
	 * Unused method
	 */
	@Override
	public ReceivedUrl getLoadMoreUrl() {
		return null;
	}

	/**
	 * @return a List that can be displayed on screen. Will return either the list that maps to the current
	 * cache key, or a new empty list.
	 */
	@Override
	public List<?> getDataList() {
		List<?> currentList = null;
		if(currentListKey != null && tableListsCache != null) {
			currentList = tableListsCache.get(currentListKey).getDataList();
		}
		//Ensure that the returned list is not null.
		if(currentList == null) {
			currentList = new ArrayList<LoadMoreDetail>();
		}
		
		return currentList;
	}
	
	/**
	 * Get the footer that should be shown at the top of the list.
	 */
	public TableLoadMoreFooter getFooter() {
		return (TableLoadMoreFooter)getView().findViewById(R.id.footer);
	}
	
	/**
	 * Refresh the pull to reset listener so that it can load more
	 */
	@Override
	public void refreshListener(){
		if(getActivity() != null) {
			table.setMode(Mode.PULL_FROM_END);
			table.onRefreshComplete();
			table.setShowViewWhileRefreshing(true);
			table.getLoadingLayoutProxy().
									setLoadingDrawable(this.getResources().getDrawable(R.drawable.load_more_arrow_release));
			getLoadMoreFooter().showDone();
		}
	}
	
	/**
	 * 
	 * @return the current key that is being used to retrieve data from cache.
	 */
	public Enum<?> getCurrentListKey() {
		return currentListKey;
	}
	
	/**
	 * Unused method
	 */
	@Override
	public Map<String, ReceivedUrl> getLinks() {
		return new HashMap<String, ReceivedUrl>();
	}

	/**
	 * Unused method
	 */
	@Override
	public void setLinks(final Map<String, ReceivedUrl> newLinks) {
		Log.e(LoadMoreBaseTable.class.getSimpleName(),
				"Do not use setLinks on " + LoadMoreBaseTable.class.getSimpleName());
	}
	
	/**
	 * Unused method
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
	}
	
	//-------------------------------------------------- Private Methods --------------------------------------------------
	
	/**
	 * Saves the current scroll position on screen to a Bundle so it can be restored later.
	 * @param bundle the Bundle to save this information to.
	 */
	private void saveTableScrollPositionToBundle(final Bundle bundle) {
		if(bundle != null) {
			if(table.getRefreshableView() != null) {
				final int scrollY = table.getRefreshableView().getFirstVisiblePosition();
				bundle.putInt(SCROLL_POSITION, scrollY);
				final View element = table.getRefreshableView().getChildAt(0);
				if(element != null) {
					bundle.putInt(LAST_VISIBLE_LIST_ITEM, element.getTop());
				}
			}
		}
	}
	
	/**
	 * Restores data for this fragment that was saved during an configuration change.
	 * 
	 * @param args a Bundle that contains information that can be used to restore the state of this Fragment.
	 */
	private void restoreArgumentsData(final Bundle args) {
		savedIndex = args.getInt(SELECTED_BUTTON_KEY, 0);
		currentListKey = (Enum<?>)args.getSerializable(BankExtraKeys.CACHE_KEY);
		savedScroll = args.getInt(SCROLL_POSITION, 0); 
		savedTopElement = args.getInt(LAST_VISIBLE_LIST_ITEM, 0);
		
		@SuppressWarnings("unchecked")
		final HashMap<Enum<?>, LoadMoreList> bundleMap = (HashMap<Enum<?>, LoadMoreList>)args.getSerializable(CACHED_MAP);
		if(bundleMap != null && bundleMap.size() > 0) {
			tableListsCache.putAll(bundleMap);
		}
	}
	
	/**
	 * Takes a Bundle and looks for a list that can be put into the local cache for this Fragment.
	 * @param args a Bundle that contains a LoadMoreList to act as the first list to display. Once displayed,
	 * it will be cleared from the passed bundle.
	 */
	private void loadDefaultListToCache(final Bundle args) {
		final LoadMoreList toCache = (LoadMoreList)args.getSerializable(BankExtraKeys.PRIMARY_LIST);
		if(currentListKey != null  && toCache != null) {
			tableListsCache.put(currentListKey, toCache);
			
			//Clear the value in the args bundle so it doesnt show up again.
			args.putSerializable(BankExtraKeys.PRIMARY_LIST, null);
		}
	}

	/**
	 * Set up the table
	 */
	private void setUpTable(){
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				tableDefaults();
				Looper.loop();
			}
		});
	}
	
	/**
	 * Setup the table with default values.
	 */
	private void tableDefaults() {
		table.setMode(Mode.DISABLED);
		table.getLoadingLayoutProxy().setPullLabel(getString(R.string.table_pull_load_more));
		table.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.table_release_to_update));
		table.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.table_dotted_line));
		table.getLoadingLayoutProxy().setLoadingDrawable(
												getResources().getDrawable(R.drawable.load_more_arrow_release));
		table.setShowViewWhileRefreshing(true);
		table.setShowIndicator(false);
		table.getLoadingLayoutProxy().setRefreshingLabel(StringUtility.EMPTY);
		table.setScrollingWhileRefreshingEnabled(false);
		table.setOnRefreshListener(getTableOnRefreshListener());
		loadHeader();
		setupTableAdapter();
	}
	
	/**
	 * 
	 * @return an OnRefreshListener for the pull to refresh table.
	 */
	private OnRefreshListener<ListView> getTableOnRefreshListener() {
		return new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
				table.getLoadingLayoutProxy().setLoadingDrawable(null);
				table.setShowViewWhileRefreshing(false);
				loadMoreIfAvailable();
			}
		};	
	}

	/**
	 * Setup the list adapter for the pull to refresh table.
	 */
	private void setupTableAdapter() {	
		if(table != null && table.getRefreshableView().getAdapter() == null) {
			LoadMoreList detailList = null;
			if(currentListKey != null) {
				LoadMoreList list = tableListsCache.get(currentListKey);
				
				if(list == null) {
					//Fallback to main cache.
					list = BankUser.instance().getCachedActivityMap().get(currentListKey);
				}
				if(list != null) {
					detailList = list;
					tableAdapter = new LoadMoreTableAdapter(getActivity(), detailList);
					uiHandler.post(new Runnable() {
						
						@Override
						public void run() {
							table.setAdapter(tableAdapter);
							tableAdapter.notifyDataSetChanged();
						}
					});
					
					final int waitTime = 200;
					uiHandler.postDelayed(getShowViewRunnable(), waitTime);
				}
			}
		}	
	}

	/**
	 * The list will automatically show an empty list message if it is empty, this
	 * method will override that message from appearing.
	 */
	private void hideEmptyListMessage() {
		if(header != null) {
			header.hideMessage();
		}
	}
	
	/**
	 * Shows a message at the top of the table, between the row of buttons and the column headers.
	 */
	private void showEmptyListMessage() {
		if(header != null) {
			header.setCustomMessage(getEmptyListMessageForEnum(currentListKey));
			header.showMessage();
		}
	}
	
	/**
	 * Do a postDelayed so that we can smoothly animate this fragment into view
	 * after it has finished loading.
	 */
	private Runnable getShowViewRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				makeVisible();
				restoreTableScroll();
			}
		};
	}	
	
	/**
	 * Restores the table scroll position to a saved position.
	 */
	private void restoreTableScroll() {
		final ListView tableList = table.getRefreshableView();
		if(tableList != null) {
			tableList.setSelectionFromTop(savedScroll, savedTopElement);
		}
	}

	/**
	 * Method that is called when the adapter gets to the bottom of the list.  
	 * This will show the go to top or show the loading bar for most fragments.
	 */
	private void loadMoreIfAvailable() {
		if(getCurrentList().canLoadMore()){
			final ReceivedUrl url = getCurrentList().getLoadMoreUrl();
			getLoadMoreFooter().showLoading();
			loadMore(url.url);
		} else {
			table.setMode(Mode.DISABLED);
			getLoadMoreFooter().showDone();
		}
	}
	
	/**
	 * Get the header that should be shown at the top of the list.
	 */
	private void loadHeader() {
		if(header == null) {
			asyncHeaderLoad();
		}else {
			final ListView list = getTable().getRefreshableView();
			addHeaderToList(list);
		}
	}
	
	
	/**
	 * When the fragment is ready to be shown, clear any visible modals/dialogs and close the drawer.
	 * Should be called after all loading has finished.
	 */
	private void makeVisible() {
		if(DiscoverModalManager.getActiveModal() != null) {
			DiscoverModalManager.clearActiveModal();
		}
		
		final Activity currentActivity = getActivity();
		if(currentActivity != null && currentActivity instanceof BaseFragmentActivity) {
			((BaseFragmentActivity)currentActivity).hideSlidingMenuIfVisible();
		}
	}
	
	/**
	 * Inflate the header for the table on a thread, and then add it to the list view from the main thread.
	 */
	private void asyncHeaderLoad() {
		final ListView list = getTable().getRefreshableView();
		header = new LoadMoreTableHeader(getActivity());
		addButtonsToHeader(header);

		setTableTitles((TableTitles)header.findViewById(R.id.table_titles));
		header.hideSecondaryMessage();
		//A runnable that will set the header of the list to the header that was just created.
		final Runnable updateHeaderRunnable = new Runnable() {
			
			@Override
			public void run() {
				addHeaderToList(list);				
			}
		};
		
		//Queue the UI update of the header to the main thread.
		uiHandler.post(updateHeaderRunnable);
	}
	
	/**
	 * Adds an instantiated header to the ListView.
	 * @param list the ListView to add the header to.
	 */
	private void addHeaderToList(final ListView list) {
		if(list != null) {
			list.addHeaderView(header, null, false);
			header.setSelectedButton(savedIndex);
		}
	}


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
	 * 
	 * @return a LoadMoreList that corresponds to the currentListKey in the cached hash map.
	 */
	private LoadMoreList getCurrentList() {
		LoadMoreList currentList = null;
		
		if(currentListKey != null) {
			currentList = tableListsCache.get(currentListKey);
		}
		
		if(currentList == null) {
			currentList = new ListTransferDetail();
		}
		
		return currentList;
	}
	
	//-------------------------------------------------- Protected Methods --------------------------------------------------

	
	/**
	 * 
	 * @return the TableLoadMoreFooter for this table.
	 */
	protected TableLoadMoreFooter getLoadMoreFooter() {
		return footer;
	}

	/**
	 * 
	 * @return a PullToRefreshListView.
	 */
	protected PullToRefreshListView getTable() {
		return table;
	}

	//----------------------------------------------------- Inner Class -----------------------------------------------------

	/**
	 * The table adapter for the load more list.
	 * 
	 * @author scottseward
	 *
	 */
	public class LoadMoreTableAdapter extends ArrayAdapter<LoadMoreDetail> {
		/**The LoadMoreList that will be used*/
		private LoadMoreList detailList = new ListTransferDetail();
		
		/**The List of data that will be displayed on screen*/
		private List<LoadMoreDetail>listData = new ArrayList<LoadMoreDetail>();
		
		public LoadMoreTableAdapter(final Context context, final LoadMoreList detailList) {
			super(context, 0);
			setData(detailList);
		}
		
		/**
		 * Set the LoadMoreList that is used for
		 * @param detailList
		 */
		@SuppressWarnings("unchecked")
		public final void setData(final LoadMoreList detailList) {
			this.detailList = detailList;
			if(detailList != null) {
				listData = (List<LoadMoreDetail>)detailList.getDataList();
			}
		}
		
		public LoadMoreTableAdapter(final Context context, final int resource, final int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
		
		/**
		 * Notify the adapter that the dataset has changed and that it should refresh its elements
		 * to show the new data.
		 * This method also handles showing the empty list message and handling if the footer will
		 * let the user attempt to load more data or not.
		 */
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			final boolean listIsEmpty = detailList == null || detailList.getDataList().size() < 1;
			final boolean canLoadMore = detailList != null && detailList.canLoadMore();
			
			if(listIsEmpty) {
				showEmptyListMessage();
				table.setMode(Mode.DISABLED);
			}else {
				hideEmptyListMessage();
				if(canLoadMore) {
					refreshListener();
				}
			}
			
			setIsLoadingMore(false);
			getLoadMoreFooter().showDone();
		}

		/**
		 * @return the number of elements that can be shown on screen.
		 */
		@Override
		public int getCount() {
			int tableSize = 0;
			
			if(detailList != null && detailList.getDataList() != null) {
				tableSize = detailList.getDataList().size();
			}
		
			return tableSize;
		}
		
		@Override
		public View getView(final int position, final View view, final ViewGroup parent) {
			View localView = view;
			ViewHolder holder = null;
			
			if(listData != null && position < listData.size()) {
				final LoadMoreDetail detail = listData.get(position);
				
				/**If the view is null, create a new one*/
				if(null == localView || !(localView.getTag() instanceof ViewHolder)){
					if(detail != null){
						holder = new ViewHolder();
						localView = initTableCellForPositionUsingHolder(position, holder);
					}
				/**Else reuse the old one*/
				}else{
					holder = (ViewHolder) view.getTag();
				}
				
				if(detail != null) {
					holder.date.setText(detail.getDate());
					holder.amount.setText(detail.getAmount());
					holder.description.setText(detail.getDescription());
					
					showRecurringLogo(detail, holder);
		
					localView.setBackgroundResource((holder.pos % 2 == 0) ? R.drawable.common_table_list_item_selector : 
																			R.drawable.common_table_list_item_gray_selector);
				}
			}
			
			return localView;
		}
		
		/**
		 * Inflates a table cell layout, accesses the view elements and assigns the holder references to it,
		 * and returns the view.
		 * @param position the index of the data set and table that this cell represents.
		 * @param holder a ViewHolder to hold references to the view elements that are used.
		 * @return a newly created View.
		 */
		private View initTableCellForPositionUsingHolder(final int position, final ViewHolder holder) {
			final View localView = LayoutInflater.from(getContext()).inflate(R.layout.bank_table_item, null);
			holder.date = (TextView) localView.findViewById(R.id.date);
			holder.description = (TextView) localView.findViewById(R.id.description);
			holder.amount = (TextView) localView.findViewById(R.id.amount);
			holder.recurringImage = (ImageView)localView.findViewById(R.id.reocurring);
			holder.pos = position;
			return localView;
		}
		
		/**
		 * Shows the recurring logo if needed.
		 * @param detail the detail object that holds data about a potential recurring transfer.
		 * @param holder the ViewHolder that has a reference to the recurringImage ImageView.
		 */
		private void showRecurringLogo(final LoadMoreDetail detail, final ViewHolder holder) {
			if(holder != null && detail != null) {
				if(detail.isRecurring()) {
					holder.recurringImage.setVisibility(View.VISIBLE);
				}else {
					holder.recurringImage.setVisibility(View.GONE);
				}
			}
		}
		
	}

}
