package com.discover.mobile.common.bank.payment;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListPaymentDetail implements Serializable{

	private static final long serialVersionUID = -6747095973097739750L;

	@JsonProperty("Payments")
	List<PaymentDetail> payments;
}
