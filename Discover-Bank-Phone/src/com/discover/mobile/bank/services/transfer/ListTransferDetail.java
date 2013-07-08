package com.discover.mobile.bank.services.transfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.ui.table.LoadMoreList;
/**
 * A ListTransferDetail object is an object that can map to a GetTransfersServiceCall result.
 * 
 * @author scottseward
 *
 */
public class ListTransferDetail implements Serializable, LoadMoreList {
	private static final long serialVersionUID = 7778378067688907299L;
	
	/**The list of transfers downloaded from the server */
	public List<TransferDetail> transfers = new ArrayList<TransferDetail>(0);

	/**List of links for for this object*/
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	/**
	 * If this list has more results that can be retrieved from the server.
	 */
	@Override
	public boolean canLoadMore() {
		return getLoadMoreUrl() != null;
	}

	/**
	 * Returns the URL that can be used to retrieve the next set of data during a load more call.
	 */
	@Override
	public ReceivedUrl getLoadMoreUrl() {
		ReceivedUrl url = null;
		
		if(links != null) {
			url = links.get(ListActivityDetail.NEXT);
		}
		
		return url;
	}

	/**
	 * Returns the list of data that can be put into a ListView.
	 */
	@Override
	public List<?> getDataList() {
		return transfers;
	}

	/**
	 * Will append data from the passed Bundle to the current list of data for this object.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addData(final Bundle data) {
		if(data != null) {
			final ListTransferDetail list = (ListTransferDetail)data.getSerializable(LoadMoreList.APPEND_LIST_KEY);
			final boolean hasDataAndCanAppend = list != null && list.getDataList() != null && transfers != null;
			
			if(hasDataAndCanAppend) {
				transfers.addAll((List<TransferDetail>)list.getDataList());
			}
		}
	}

	/**
	 * The links that are associated with LoadMore
	 */
	@Override
	public Map<String, ReceivedUrl> getLinks() {
		return links;
	}
	
	/**
	 * Set the links for this object.
	 */
	@Override
	public void setLinks(final Map<String, ReceivedUrl> newLinks) {
		this.links = newLinks;
	}
	
}
