package com.discover.mobile.bank.services.atm.autocomplete;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionsList implements Serializable {

	/**
	 * auto generated UID
	 */
	private static final long serialVersionUID = -8202182061060154167L;

	@JsonProperty("predictions")
	public List<Prediction> predicationList;
	
}
