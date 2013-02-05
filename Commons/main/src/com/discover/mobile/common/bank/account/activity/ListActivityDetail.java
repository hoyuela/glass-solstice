package com.discover.mobile.common.bank.account.activity;

import java.util.List;

/**
 * A ActivityDetails List object and passed to the application layer.
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
public class ListActivityDetail {

	/**List of activity details*/
	List<ActivityDetail> activities;
}
