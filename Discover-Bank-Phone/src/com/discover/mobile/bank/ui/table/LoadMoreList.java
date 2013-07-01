package com.discover.mobile.bank.ui.table;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

import com.discover.mobile.bank.services.json.ReceivedUrl;

/**
 * An interface for a list that can be used in a load more table.
 * 
 * @author scottseward
 *
 */
public interface LoadMoreList {
	/**The key used to pass data in the Bundle for the addData method. */
	public static final String APPEND_LIST_KEY = "lmlak";
	
	/**
	 * 
	 * @return if the current state of the list allows loading more data.
	 */
	boolean canLoadMore();
	
	/**
	 * 
	 * @return the URL that will return data from a LoadMore service call.
	 */
	ReceivedUrl getLoadMoreUrl();
	
	
	/**
	 * @return the list of data
	 */
	List<?> getDataList();
	
	/**
	 * Append data to the list
	 */
	void addData(final Bundle data);
	
	Map<String, ReceivedUrl> getLinks();
	
	void setLinks(Map<String, ReceivedUrl> newLinks);
	
}
