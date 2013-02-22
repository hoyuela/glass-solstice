package com.discover.mobile.card.services.account;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriesDetail implements Serializable {

	/** serialid */
	private static final long serialVersionUID = 214059387358805989L;

	@JsonProperty("category")
	public List<CategoryDetail> categories;

	@JsonProperty("otherCategory")
	public List<CategoryDetail> otherCategories;

	@JsonProperty("outageModeVal")
	public String outageMode;
	
}
