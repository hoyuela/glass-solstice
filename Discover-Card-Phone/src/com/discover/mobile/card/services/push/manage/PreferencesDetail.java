package com.discover.mobile.card.services.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single preference set by the user
 * @author jthornton
 *
 */
public class PreferencesDetail implements Serializable{
	
	/**String representing the user acceptance of the T&C is pending*/
	public static final String PENDING = "P";
	
	/**String representing the user accepted the T&C*/
	public static final String ACCEPTED = "Y";
	
	/**String representing text preference*/
	public static final String TEXT_PARAM = "SMRM";
	
	/**String representing text preference*/
	public static final String PUSH_PARAM = "PNRM";

	/**Unique serial identifier*/
	private static final long serialVersionUID = 4993285719297737090L;

	/**List of params associated with this preference*/
	@JsonProperty("params")
	public List<PushManageCategoryParamDetail> params;
	
	/**Category of this preference*/
	@JsonProperty("categoryId")
	public String categoryId;
	
	/**String representing if the user accepts the T&C*/
	@JsonProperty("custAccptInd")
	public String accepted;
	
	/**Code of the preference*/
	@JsonProperty("prefTypeCode")
	public String prefTypeCode;
}
