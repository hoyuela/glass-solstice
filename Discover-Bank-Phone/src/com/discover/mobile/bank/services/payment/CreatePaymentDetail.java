package com.discover.mobile.bank.services.payment;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Detail used to send a payment.
 * 
 * JSON example:
 * { 
 *  "payee" : "00000000004", // id of the payee 
 *  "amount" : 2344, // amount for the payment, $23.44 
 *  "paymentMethod" : 1, // id of the account 
 *  "deliverBy" : "2013-03-28T00:00:00Z" // date for the payment to be delivered by 
 *  "memo" : "this memo field is optional." 
 * } 
 * 
 */
public class CreatePaymentDetail implements Serializable {
	
	/** Identifier id for class */
	private static final long serialVersionUID = 7318356891841603871L;

	/* ID of Payee */
	@JsonProperty("payee")
	public String payee;
	
	/* $23.44 should be sent as 2344 */
	@JsonProperty("amount")
	public int amount;
	
	/* ID of Payment Account */
	@JsonProperty("paymentMethod")
	public int paymentMethod; 
	
	/* Date to deliver as "3012-10-06T00:00:00Z" */
	@JsonProperty("deliverBy")
	public String deliverBy;

	/* Memo (if it exists) */
	@Nullable
	@JsonProperty("memo")
	public String memo;
}
