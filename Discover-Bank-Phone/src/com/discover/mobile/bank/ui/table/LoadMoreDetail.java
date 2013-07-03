package com.discover.mobile.bank.ui.table;


/**
 * If a detail object, one that contains details about a transaction, implements
 * this interface, it can then be presented easily in a LoadMoreBaseTable without
 * the need of implementing a custom adapter for the object.
 * @author scottseward
 *
 */
public interface LoadMoreDetail {

	/**
	 * 
	 * @return a formatted String that can be presented on screen as a date.
	 */
	String getDate();
	
	/**
	 * 
	 * @return The description of the detail that is being presented on screen.
	 */
	String getDescription();
	
	/**
	 * 
	 * @return a formatted dollar amount that can be presented on screen.
	 */
	String getAmount();
	
	/**
	 * 
	 * @return if the detail is a recurring transfer.
	 */
	boolean isRecurringTransfer();
	
	/**
	 * @return if the detail is an outbound transfer
	 */
	boolean isOutboundTransfer();
	
}
