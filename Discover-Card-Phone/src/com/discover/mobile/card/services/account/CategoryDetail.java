package com.discover.mobile.card.services.account;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryDetail implements Serializable {

	/** generated serialid */
	private static final long serialVersionUID = -800028317175261418L;

	@JsonProperty("categoryCode")
	public String categoryCode;
	
	@JsonProperty("categoryDesc")
	public String categoryDesc;
	
	public CategoryDetail(String catCode, String catDesc) {
		
		categoryCode = catCode;
		categoryDesc = catDesc;
	}
	
	public CategoryDetail() {

	}
	
	/**
	 * @return the Category Description that will be displayed in the spinner.
	 */
	public String toString() {
		
		return categoryDesc;
	}
}
