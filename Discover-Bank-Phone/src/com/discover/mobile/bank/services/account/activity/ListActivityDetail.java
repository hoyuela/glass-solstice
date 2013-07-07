package com.discover.mobile.bank.services.account.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.table.LoadMoreList;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A ActivityDetails List object and passed to the application layer.
 * 
 * API Call: /api/accounts/{id}/activity
 * 
 * The following is an example of the Customer JSON response:
 * 
 * [
 *      {
 *         "id" : "123182309128",
 *         "description" : "CUSTOMER DEPOSIT",
 *         "amount" : 35000,
 *         "dates": {
 *                          "date" : "20120416T00:00:00Z",
 *                          "dateClassifier" : "POSTED_DATE",
 *                          "formattedDate" : " 04/16/2012"
 *                     },
 *         "balance" : 47000,
 *         "transactionType" : "DEPOSIT"
 *      },
 *      {
 *         "id" : "123182309129",
 *         "description" : "CUSTOMER DEPOSIT",
 *         "amount" : 12000,
 *         "dates": {
 *                          "date" : "20120415T00:00:00Z",
 *                          "dateClassifier" : "POSTED_DATE",
 *                          "formattedDate" : " 04/15/2012"
 *                     },
 *         "balance" : 12000,
 *        "transactionType" : "DEPOSIT"
 *      }
 * ]
 * 
 * @author jthornton
 *
 */
public class ListActivityDetail implements Serializable{

	/**Unique identifier for the object*/
	private static final long serialVersionUID = 8207177094396952741L;

	/**String to get the next url from the links onject*/
	public static final String NEXT = "next";

	/**List of activity details*/
	public List<ActivityDetail> activities;

	/**Specifies what type of activity are in activities*/
	@JsonIgnore
	public ActivityDetailType type;

	/**List of links for for this object*/
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	public ListActivityDetail() {
		activities = new ArrayList<ActivityDetail>();
		type = null;
	}
	
	/**
	 * Converts a LoadMoreList to a ListActivityDetail object. Used when passing a LoadMoreList
	 * to a ViewPager that expects a ListActivityDetail.
	 * @param listToConvert a LoadMoreList
	 * @param type the type of ActivityDetail that the list represents.
	 */
	@SuppressWarnings("unchecked")
	public ListActivityDetail(final LoadMoreList listToConvert, final ActivityDetailType type) {
		final List<TransferDetail> list = (List<TransferDetail>) listToConvert.getDataList();
		
		if(list != null && list.size() > 0) {
			activities = new ArrayList<ActivityDetail>(list.size());
			activities.addAll(getActivityDetailListForLoadMoreList(list));
		}
		else {
			activities = new ArrayList<ActivityDetail>(0);
		}
		
		this.type = type;
	}
	
	/**
	 * Maps a list of Transfer Detail objects to a list of ActivityDetail objects.
	 * @param currentList a List of TransferDetail objects ot map to a List of ActivityDetail objects.
	 * @return a List of ActivityDetail objects.
	 */
	private List<ActivityDetail> getActivityDetailListForLoadMoreList(final List<TransferDetail> currentList) {

		List<ActivityDetail> mappedList = null;
		
		if(currentList != null && currentList.size() > 0) {
			mappedList = new ArrayList<ActivityDetail>(currentList.size());
			final List<TransferDetail>transferList = currentList;
			final int listSize =  transferList.size();
			
			for(int i = 0; i < listSize; ++i) {
				final TransferDetail detailItem = transferList.get(i);
				final ActivityDetail activityItem = new ActivityDetail();
				
				activityItem.fromAccount = detailItem.fromAccount;
				activityItem.toAccount = detailItem.toAccount;
				activityItem.amount = detailItem.amount;
				activityItem.sendDate = detailItem.sendDate;
				activityItem.deliverBy = detailItem.deliverBy;
				activityItem.frequency = detailItem.frequency;
				activityItem.durationType = detailItem.durationType;
				activityItem.links = detailItem.links;
				activityItem.type = ActivityDetail.TYPE_TRANSFER;
				
				mappedList.add(activityItem);
			}
			
		}else {
			mappedList = new ArrayList<ActivityDetail>(0);
		}
		
		return mappedList;
	}
}
