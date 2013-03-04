package com.discover.mobile.card.services.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * A single preference set by the user, used in the posting
 * @author jthornton
 *
 */
public class PostPrefDetail implements Serializable{

	/**Unique serial identifier*/
	private static final long serialVersionUID = -1670733844579236615L;

	/**String representing the user acceptance of the T&C is pending*/
	public static final String PENDING = "P";
	
	/**String representing the user accepted the T&C*/
	public static final String ACCEPTED = "Y";
	
	/**String representing the user declined the T&C*/
	public static final String TEXT_PARAM = "SMRM";
	
	/**String representing text preference*/
	public static final String PUSH_PARAM = "PNRM";
	
	/**List of params associated with this preference*/
	@JsonProperty("params")
	public List<PostPrefParam> params;
	
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
