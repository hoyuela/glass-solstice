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

public class ListTransferDetail implements Serializable, LoadMoreList {
	private static final long serialVersionUID = 7778378067688907299L;
	
	/**The list of transfers downloaded from the server */
	public List<TransferDetail> transfers = new ArrayList<TransferDetail>(0);

	/**List of links for for this object*/
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	@Override
	public boolean canLoadMore() {
		return getLoadMoreUrl() != null;
	}

	@Override
	public ReceivedUrl getLoadMoreUrl() {
		ReceivedUrl url = null;
		
		if(links != null) {
			url = links.get(ListActivityDetail.NEXT);
		}
		
		return url;
	}

	@Override
	public List<?> getDataList() {
		return transfers;
	}

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

	@Override
	public Map<String, ReceivedUrl> getLinks() {
		return links;
	}

	@Override
	public void setLinks(final Map<String, ReceivedUrl> newLinks) {
		this.links = newLinks;
	}
	
}
