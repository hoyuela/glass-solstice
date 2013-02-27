package com.discover.mobile.bank.services.payment;

/**
 * This enum is used to specify the query type of request when sending a Get Payment Service request using GetPaymentServiceCall.
 * @author henryoyuela
 *
 */
public enum PaymentQueryType {
	/**Query string to retrieve all the payments*/
	ALL,

	/**Query string to retrieve all the scheduled payments*/
	SCHEDULED,

	/**Query string to retrieve all the cancelled payments*/
	CANCELLED,

	/**Query string to retrieve all the completed payments*/
	COMPLETED 
}
