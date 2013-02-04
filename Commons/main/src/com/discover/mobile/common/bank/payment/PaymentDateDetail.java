package com.discover.mobile.common.bank.payment;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentDateDetail implements Serializable{

	private static final long serialVersionUID = 2688365754917618982L;

	@JsonProperty("deliverBy")
	public List<DeliveryDateDetail> date;

}
