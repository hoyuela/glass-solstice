package com.discover.mobile.section.account.recent;

import java.util.List;

import com.discover.mobile.common.account.recent.GetTransactionDetails;
import com.discover.mobile.common.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.account.recent.TransactionDetail;

/**
 * Singleton class that helps with the rotations of the recent activity screen. This
 * is used because that screen can have massive amounts of data to store and the processing time
 * to store all of that data in the bundle could become a cumbersome.
 * 
 * ****
 *  Note: Make sure that this is clear once the rotation is complete, there is no need to keep
 *  this object around.
 * ****
 * @author jthornton
 *
 */
public class RecentActivityRotationHelper {
	
	/**Current range showing transactions*/
	private RecentActivityPeriodDetail currentRange;
	
	/**All the ranges available to be displayed*/
	private RecentActivityPeriodsDetail periods;

	/**Instance of the class*/
	private static RecentActivityRotationHelper helper;
	
	/**List of transactions that the layout contains*/
	private List<TransactionDetail> pending;
	
	/**List of transactions that the layout contains*/
	private List<TransactionDetail> posted;

	/**Activity details from the server*/
	private GetTransactionDetails transactions;
	
	/**Boolean letting the fragment know it has data in it*/
	private boolean hasData = false;
	
	/**
	 * Private constructor for the class
	 */
	private RecentActivityRotationHelper(){}
	
	/**
	 * Get the helper
	 * @return - the hepler
	 */
	public static RecentActivityRotationHelper getHelper(){
		if(null == helper){
			helper = new RecentActivityRotationHelper();
		}
		return helper;
	}

	/**
	 * Clear the data out of the helper
	 */
	public void clearHelper(){
		helper = new RecentActivityRotationHelper();
	}

	/**
	 * @return the currentRange
	 */
	public RecentActivityPeriodDetail getCurrentRange() {
		return currentRange;
	}

	/**
	 * @param currentRange the currentRange to set
	 */
	public void setCurrentRange(final RecentActivityPeriodDetail currentRange) {
		this.currentRange = currentRange;
	}

	/**
	 * @return the periods
	 */
	public RecentActivityPeriodsDetail getPeriods() {
		return periods;
	}

	/**
	 * @param periods the periods to set
	 */
	public void setPeriods(final RecentActivityPeriodsDetail periods) {
		this.periods = periods;
	}

	/**
	 * @return the pending
	 */
	public List<TransactionDetail> getPending() {
		return pending;
	}

	/**
	 * @param pending the pending to set
	 */
	public void setPending(final List<TransactionDetail> pending) {
		this.pending = pending;
	}

	/**
	 * @return the posted
	 */
	public List<TransactionDetail> getPosted() {
		return posted;
	}

	/**
	 * @param posted the posted to set
	 */
	public void setPosted(final List<TransactionDetail> posted) {
		this.posted = posted;
	}

	/**
	 * @return the transactions
	 */
	public GetTransactionDetails getTransactions() {
		return transactions;
	}

	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(GetTransactionDetails transactions) {
		this.transactions = transactions;
	}

	/**
	 * @return the hasData
	 */
	public boolean isHasData() {
		return hasData;
	}

	/**
	 * @param hasData the hasData to set
	 */
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}
}
